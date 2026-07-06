package service.impl;

import dao.HeroDao;
import dao.impl.HeroDaoImpl;
import entity.Hero;
import entity.HeroStats;
import vo.HeroVO;
import exception.ServiceException;
import service.HeroService;

import java.util.List;

/**
 * 角色業務邏輯實作類別 (HeroServiceImpl)
 * 負責協調與呼叫 DAO 層以完成選擇角色大廳的業務規則。
 */
public class HeroServiceImpl implements HeroService {

    // 引入角色資料存取層 (DAO) 的實作
    private HeroDao heroDao = new HeroDaoImpl();

    @Override
    public List<HeroVO> showPlayerHeroById(int playerId) throws ServiceException {
        try {
            // 💡 邏輯：直接透傳給 DAO 去查詢檢視表 (View) 資料
            return heroDao.selectByPlayerId(playerId);
        } catch (Exception e) {
            // 捕獲任何 SQL 或底層異常，包裝成自訂業務例外 ServiceException 向上拋出給 UI 處理
            throw new ServiceException("載入角色清單失敗，錯誤原因: " + e.getMessage());
        }
    }

    @Override
    public boolean deletePlayerHero(int heroId) throws ServiceException {
        try {
            // 💡 邏輯：直接呼叫 DAO 刪除角色，DAO 內因 CASCADE 關聯會一併自動清除戰鬥數值
            boolean success = heroDao.deleteById(heroId);
            if (!success) {
                throw new ServiceException("刪除失敗：找不到該角色資料，可能先前已被刪除！");
            }
            return true;
        } catch (ServiceException e) {
            throw e; // 如果是我們主動丟出的防呆例外，直接原樣拋出
        } catch (Exception e) {
            throw new ServiceException("刪除角色過程中發生未知異常，錯誤原因: " + e.getMessage());
        }
    }

    @Override
    public void createHero(Hero hero, HeroStats stats) throws ServiceException {
        // 💡 檢查點一：欄位驗證 (防呆阻擋，名稱不能為空字串或純空白字元)
        if (hero.getName() == null || hero.getName().trim().isEmpty()) {
            throw new ServiceException("創建失敗：名稱欄位不能為空！");
        }

        // 💡 檢查點二：重複名稱檢查 (檢查該玩家底下是否已經有相同名字的角色)
        List<HeroVO> existing = heroDao.selectByPlayerIdAndHeroName(hero.getPlayerId(), hero.getName());
        if (existing != null && !existing.isEmpty()) {
            throw new ServiceException("創建失敗：名稱已被使用！");
        }

        // 💡 檢查點三：深度防禦 (雙重防線)
        // 在 Service 層二次檢驗玩家分配的點數。我們用目前的數值減去基礎值，除以配點權重，算出配了幾點：
        int hpPointsAllocated = (stats.getHp() - 100) / 15;   // 1 點 = 15 HP
        int atkPointsAllocated = stats.getAtk() - 5;          // 1 點 = 1 ATK
        int defPointsAllocated = stats.getDef() - 5;          // 1 點 = 1 DEF
        int speedPointsAllocated = stats.getSpeed() - 5;      // 1 點 = 1 Speed
        
        int totalAllocated = hpPointsAllocated + atkPointsAllocated + defPointsAllocated + speedPointsAllocated;
        
        // 如果分配點數總和不等於 5 點，代表資料異常（可能是外掛或非法請求）
        if (totalAllocated != 5) {
            throw new ServiceException("創建失敗：還有剩餘的屬性點未分配！"); // 統一的輸出訊息
        }

        // 💡 執行寫入：當所有驗證都通過，才呼叫 DAO 執行交易寫入
        try {
            boolean success = heroDao.insert(hero, stats);
            if (!success) {
                throw new ServiceException("創建失敗：資料庫寫入失敗！");
            }
        } catch (Exception e) {
            throw new ServiceException("創建角色時發生資料庫異常，原因: " + e.getMessage());
        }
    }
}
