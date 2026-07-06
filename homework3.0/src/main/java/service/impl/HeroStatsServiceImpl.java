package service.impl;

import java.util.List;

import dao.HeroDao;
import dao.HeroEquipmentDao;
import dao.impl.HeroDaoImpl;
import dao.impl.HeroEquipmentDaoImpl;
import vo.HeroEquipmentVO;
import vo.HeroVO;
import service.HeroStatsService;
import dto.LevelUpResult;

/**
 * 角色屬性數值服務實作類別 (HeroStatsServiceImpl)
 * 💡 負責結合角色基礎數值與已裝備屬性加成，產出最終的戰鬥數值。
 */
public class HeroStatsServiceImpl implements HeroStatsService {

    private HeroDao heroDao = new HeroDaoImpl();
    private HeroEquipmentDao heroEquipmentDao = new HeroEquipmentDaoImpl();

    @Override
    public HeroVO getCombinedStats(int heroId) {
        // 💡 1. 讀取角色的基礎數值 (從 v_hero_detail)
        HeroVO baseHero = heroDao.selectByHeroId(heroId);
        
        // 防呆：如果找不到角色，回傳 null
        if (baseHero == null) {
            return null;
        }

        // 💡 2. 讀取該角色背包裡的所有裝備
        List<HeroEquipmentVO> bagItems = heroEquipmentDao.selectByHeroId(heroId);

        // 💡 3. 遍歷裝備清單，累加所有「已穿戴 (isEquipped == true)」裝備的加成數值
        int totalHpBonus = 0;
        int totalAtkBonus = 0;
        int totalDefBonus = 0;
        int totalSpeedBonus = 0;

        for (HeroEquipmentVO item : bagItems) {
            if (item.isEquipped()) {
                totalHpBonus += item.getHpBonus();
                totalAtkBonus += item.getAtkBonus();
                totalDefBonus += item.getDefBonus();
                totalSpeedBonus += item.getSpeedBonus();
            }
        }

        // 💡 4. 將加成後的數值套入，產生總合戰力
        baseHero.setHp(baseHero.getHp() + totalHpBonus);
        baseHero.setAtk(baseHero.getAtk() + totalAtkBonus);
        baseHero.setDef(baseHero.getDef() + totalDefBonus);
        baseHero.setSpeed(baseHero.getSpeed() + totalSpeedBonus);

        return baseHero;
    }

    @Override
    public LevelUpResult addExpAndCheckLevelUp(int heroId, int expGained) {
        // 1. 讀取角色的基礎數值 (從 v_hero_detail)
        HeroVO baseHero = heroDao.selectByHeroId(heroId);
        if (baseHero == null) {
            return new LevelUpResult();
        }

        int oldLevel = baseHero.getLevel();
        int currentExp = baseHero.getExp() + expGained;
        int level = oldLevel;
        boolean leveledUp = false;

        // 💡 2. 迴圈檢查是否升級 (防止一次獲得大量經驗時連升多級)
        while (true) {
            int expNeeded = level * 100;
            if (currentExp >= expNeeded) {
                level++;
                currentExp -= expNeeded;
                leveledUp = true;
                
                // 升級屬性成長規則：基礎血量上限 +15，基礎攻擊/防禦/速度 +1
                baseHero.setHp(baseHero.getHp() + 15);
                baseHero.setAtk(baseHero.getAtk() + 1);
                baseHero.setDef(baseHero.getDef() + 1);
                baseHero.setSpeed(baseHero.getSpeed() + 1);
                baseHero.setStatPoints(baseHero.getStatPoints() + 5); // 💡 新增：每次升級額外獲得 5 點自由配點
            } else {
                break;
            }
        }

        // 更新等級與經驗值
        baseHero.setLevel(level);
        baseHero.setExp(currentExp);

        // 💡 3. 將最新計算的完整屬性值 (包含成長與屬性點) 持久化更新到 MySQL 中
        heroDao.updateStats(baseHero);

        return new LevelUpResult(leveledUp, oldLevel, level);
    }

    @Override
    public boolean allocateStatPoints(int heroId, int hpAlloc, int atkAlloc, int defAlloc, int speedAlloc) {
        // 1. 讀取角色基礎屬性 (從 v_hero_detail)
        HeroVO baseHero = heroDao.selectByHeroId(heroId);
        if (baseHero == null) {
            return false;
        }

        // 2. 驗證加點合法性 (配點總和不能超過可用點數，且至少大於 0)
        int totalAlloc = hpAlloc + atkAlloc + defAlloc + speedAlloc;
        if (totalAlloc <= 0) {
            return false;
        }
        if (totalAlloc > baseHero.getStatPoints()) {
            System.err.println("❌ 分配屬性點失敗，分配點數 [" + totalAlloc + "] 超過角色剩餘點數 [" + baseHero.getStatPoints() + "]");
            return false;
        }

        // 3. 扣除剩餘點數並提升對應的「基礎屬性」上限 (生命每點 +15，其餘 +1)
        baseHero.setStatPoints(baseHero.getStatPoints() - totalAlloc);
        baseHero.setHp(baseHero.getHp() + (hpAlloc * 15));
        baseHero.setAtk(baseHero.getAtk() + atkAlloc);
        baseHero.setDef(baseHero.getDef() + defAlloc);
        baseHero.setSpeed(baseHero.getSpeed() + speedAlloc);

        // 4. 寫入 MySQL 存檔
        return heroDao.updateStats(baseHero);
    }
}
