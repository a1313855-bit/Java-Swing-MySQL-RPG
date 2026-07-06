package service.impl;

import java.util.List;

import dao.HeroEquipmentDao;
import dao.impl.HeroEquipmentDaoImpl;
import dao.EquipmentDao;
import dao.impl.EquipmentDaoImpl;
import entity.Monster;
import entity.Equipment;
import entity.HeroEquipment;
import vo.HeroEquipmentVO;
import service.BagService;

/**
 * 背包系統業務邏輯實作類別 (BagServiceImpl)
 * 💡 負責處理背包的載入與穿戴替換規則。
 */
public class BagServiceImpl implements BagService {

    private HeroEquipmentDao heroEquipmentDao = new HeroEquipmentDaoImpl();
    private EquipmentDao equipmentDao = new EquipmentDaoImpl();

    @Override
    public List<HeroEquipmentVO> getBagItems(int heroId) {
        // 直接調用 DAO 從檢視表撈出乾淨的背包資料
        return heroEquipmentDao.selectByHeroId(heroId);
    }

    @Override
    public boolean toggleEquipStatus(int heroEquipmentId, int heroId) {
        // 💡 步驟 1：先撈出該角色目前所有的背包裝備
        List<HeroEquipmentVO> bagItems = heroEquipmentDao.selectByHeroId(heroId);
        
        // 💡 步驟 2：尋找玩家點擊的那件裝備
        HeroEquipmentVO targetItem = null;
        for (HeroEquipmentVO item : bagItems) {
            if (item.getId() == heroEquipmentId) {
                targetItem = item;
                break;
            }
        }

        // 防呆：如果找不到這件裝備，直接回傳失敗
        if (targetItem == null) {
            System.err.println("⚠️ 找不到指定的背包裝備關係 ID: " + heroEquipmentId);
            return false;
        }

        // 💡 步驟 3：判定穿上還是脫下
        if (targetItem.isEquipped()) {
            // 情境 A：目前是已穿戴 ➡️ 玩家要「脫下」它
            return heroEquipmentDao.updateEquippedStatus(heroEquipmentId, false);
        } else {
            // 情境 B：目前在背包中 ➡️ 玩家要「穿上」它
            
            // 💡 步驟 4：自動替換檢查！
            // 尋找同一個部位 (Slot) 是否已有其他裝備被穿戴。若有，先將其脫下。
            for (HeroEquipmentVO item : bagItems) {
                // 如果部位相同，且該裝備是穿戴中的狀態，且不是我們正要穿的這一件
                if (item.getSlot().equals(targetItem.getSlot()) && item.isEquipped() && item.getId() != heroEquipmentId) {
                    System.out.println("🔄 偵測到部位 [" + item.getSlot() + "] 已有裝備 [" + item.getName() + "]，自動將其脫下。");
                    heroEquipmentDao.updateEquippedStatus(item.getId(), false);
                }
            }

            // 💡 步驟 5：穿上新裝備
            return heroEquipmentDao.updateEquippedStatus(heroEquipmentId, true);
        }
    }

    @Override
    public HeroEquipmentVO rollMonsterLoot(int heroId, Monster monster) {
        // 💡 1. 擲骰子判定是否掉落
        double dice = Math.random();
        if (dice > monster.getDropRate()) {
            return null; // 未中獎，回傳 null
        }

        int lootTier = monster.getLootTier();

        // 💡 2. 協調 EquipmentDao 讀取該品級的所有裝備範本
        List<Equipment> templates = equipmentDao.selectByTier(lootTier);
        if (templates == null || templates.isEmpty()) {
            return null;
        }

        // 💡 3. 隨機抽選一件裝備範本
        int randomIndex = (int) (Math.random() * templates.size());
        Equipment dropTemplate = templates.get(randomIndex);

        // 💡 4. 創建背包關係實體，並寫入資料庫
        HeroEquipment heroEquip = new HeroEquipment();
        heroEquip.setHeroId(heroId);
        heroEquip.setEquipmentId(dropTemplate.getId());
        heroEquip.setEquipped(false); // 預設放背包

        boolean success = heroEquipmentDao.insert(heroEquip);
        if (!success) {
            System.err.println("❌ 掉寶寫入資料庫失敗！");
            return null;
        }

        // 💡 5. 為了讓呼叫者 (BattleService) 知道掉落了什麼，我們重新查出該裝備的詳細 VO (包含中文、加成等)
        List<HeroEquipmentVO> updatedBag = heroEquipmentDao.selectByHeroId(heroId);
        if (updatedBag != null) {
            // 尋找最後加進去且符合範本 ID 的那件裝備 (因為沒有包含時間戳記，直接找最新的符合者即可)
            for (int i = updatedBag.size() - 1; i >= 0; i--) {
                HeroEquipmentVO vo = updatedBag.get(i);
                if (vo.getEquipmentId() == dropTemplate.getId() && !vo.isEquipped()) {
                    return vo; // 成功找到並回傳
                }
            }
        }

        // 降級防呆：如果撈不出 VO，就手動建一個簡單的 VO 讓日誌能印出名字
        HeroEquipmentVO fallbackVo = new HeroEquipmentVO();
        fallbackVo.setName(dropTemplate.getName());
        fallbackVo.setTier(dropTemplate.getTier());
        return fallbackVo;
    }
}
