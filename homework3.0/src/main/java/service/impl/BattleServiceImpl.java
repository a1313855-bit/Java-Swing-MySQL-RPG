package service.impl;

import java.util.ArrayList;
import java.util.List;

import dao.HeroDao;
import dao.MonsterDao;
import dao.impl.HeroDaoImpl;
import dao.impl.MonsterDaoImpl;
import entity.Monster;
import vo.HeroVO;
import vo.HeroEquipmentVO;
import service.BattleService;
import service.BagService;
import service.HeroStatsService;
import service.impl.BagServiceImpl;
import service.impl.HeroStatsServiceImpl;
import dto.BattleTickResult;
import dto.LevelUpResult;

/**
 * 💡【軟體架構學習筆記：協調型服務 (Orchestration Service) 實作類別】
 * 
 * 💡【設計原理與分工】
 *   1. 它是「有狀態的」業務邏輯管理器：
 *      它在記憶體中持有 `currentHero` (玩家)、`currentMonster` (怪物)、雙方血量及行動值累積。
 *   2. 它的職責是協調多個底層 DAO 與 Service：
 *      - `MonsterDao`：載入探索地圖的所有怪物模板。
 *      - `HeroDao`：讀取或更新角色資料。
 *      - `HeroStatsService`：處理經驗增加、升級屬性成長。
 *      - `BagService`：處理怪物戰敗後的掉寶隨機性與寫入資料庫。
 */
public class BattleServiceImpl implements BattleService {

    // --- 外部 服務與 DAO 協調對象 ---
    private MonsterDao monsterDao = new MonsterDaoImpl();
    private HeroDao heroDao = new HeroDaoImpl();
    private BagService bagService = new BagServiceImpl();
    private HeroStatsService heroStatsService = new HeroStatsServiceImpl();

    // --- 💡 戰鬥狀態核心記憶體變數 (State) ---
    private HeroVO currentHero;            // 玩家角色
    private List<Monster> mapMonsters;     // 當前地圖怪物模板清單
    private Monster currentMonster;        // 當前對戰中的怪物
    private int currentPlayerHp;           // 玩家當前血量
    private int maxPlayerHp;               // 玩家最大血量
    private int currentMonsterHp;          // 怪物當前血量
    private double playerActionVal = 0.0;  // 玩家當前行動值
    private double monsterActionVal = 0.0; // 怪物當前行動值

    private boolean isFighting = false;    // 是否在戰鬥狀態中

    @Override
    public void startBattle(HeroVO hero, int mapId) {
        this.currentHero = hero;
        this.currentPlayerHp = hero.getHp();
        this.maxPlayerHp = hero.getHp();

        // 載入該地圖的所有怪物範本
        this.mapMonsters = monsterDao.selectByMapId(mapId);
        this.isFighting = true;

        // 重置行動條並召喚第一隻怪
        this.playerActionVal = 0.0;
        this.monsterActionVal = 0.0;
        spawnNewMonster();
    }

    @Override
    public void stopBattle() {
        this.isFighting = false;
        this.currentMonster = null;
        this.mapMonsters = null;
    }

    @Override
    public BattleTickResult tick() {
        BattleTickResult result = new BattleTickResult();
        
        // 防呆：如果不在戰鬥狀態，直接回傳空狀態
        if (!isFighting || currentMonster == null) {
            result.setPlayerHp(currentPlayerHp);
            result.setMaxPlayerHp(maxPlayerHp);
            return result;
        }

        // 1. 雙方累積行動值 (攻速決定累積速度)
        playerActionVal += currentHero.getSpeed() * 0.1;
        monsterActionVal += currentMonster.getSpeed() * 0.1;

        // 2. 玩家攻擊判定
        if (playerActionVal >= 100) {
            playerActionVal = 0.0; // 重置行動條

            // 傷害公式：玩家攻擊力 - 怪物防禦力 (最低 1 點傷害)
            int damage = currentHero.getAtk() - currentMonster.getDef();
            if (damage < 1) {
                damage = 1;
            }

            currentMonsterHp -= damage;
            if (currentMonsterHp < 0) {
                currentMonsterHp = 0;
            }

            result.addLogMessage("⚔️ " + currentHero.getName() + " 發動攻擊！造成 [" + currentMonster.getName() + "] " + damage + " 點傷害！\n");

            // 檢查怪物是否死亡
            if (currentMonsterHp <= 0) {
                handleMonsterDefeat(result);
                // 💡 怪物死後，提早寫入回傳狀態並結束本輪心跳，防範死去的怪還發動攻擊
                fillResultState(result);
                return result;
            }
        }

        // 3. 怪物反擊判定
        if (monsterActionVal >= 100) {
            monsterActionVal = 0.0; // 重置行動條

            // 傷害公式：怪物攻擊力 - 玩家防禦力 (最低 1 點傷害)
            int damage = currentMonster.getAtk() - currentHero.getDef();
            if (damage < 1) {
                damage = 1;
            }

            currentPlayerHp -= damage;
            if (currentPlayerHp < 0) {
                currentPlayerHp = 0;
            }

            result.addLogMessage("💥 [" + currentMonster.getName() + "] 發動反擊！對 " + currentHero.getName() + " 造成了 " + damage + " 點傷害！\n");

            // 檢查玩家是否戰敗
            if (currentPlayerHp <= 0) {
                result.addLogMessage("💀 您被 " + currentMonster.getName() + " 擊敗了！戰鬥終止，已退回安全草原休整。\n");
                result.setPlayerDead(true);
                stopBattle();
            }
        }

        // 4. 將當前的即時數值封裝到 DTO 中，供 UI 讀取
        fillResultState(result);
        return result;
    }

    /**
     * 💡 私有輔助方法：隨機載入下一隻怪
     */
    private void spawnNewMonster() {
        if (mapMonsters == null || mapMonsters.isEmpty()) {
            return;
        }
        int randomIndex = (int) (Math.random() * mapMonsters.size());
        currentMonster = mapMonsters.get(randomIndex);
        currentMonsterHp = currentMonster.getHp();
        monsterActionVal = 0.0;
    }

    /**
     * 💡 私有輔助方法：處理打敗怪物的獎勵、經驗升級與掉寶協調
     */
    private void handleMonsterDefeat(BattleTickResult result) {
        result.addLogMessage("🎉 成功擊敗了 " + currentMonster.getName() + "！\n");
        result.addLogMessage("   獲得經驗值：+" + currentMonster.getExpReward() + "\n");
        result.setMonsterDead(true);

        // 💡 1. 呼叫 HeroStatsService 處理經驗增加與升級邏輯 (完全解耦！)
        LevelUpResult lvResult = heroStatsService.addExpAndCheckLevelUp(currentHero.getId(), currentMonster.getExpReward());
        
        if (lvResult.isLeveledUp()) {
            result.addLogMessage("👑【系統】恭喜！" + currentHero.getName() + " 升級到了 Lv." + lvResult.getNewLevel() + "！\n");
            result.addLogMessage("   (屬性上限獲得成長，可點擊下方配點按鈕進行分配！)\n");
            
            // 💡 升級後，讀取包含最新升級加成屬性的實體，同步戰鬥記憶體數值
            HeroVO combinedHero = heroStatsService.getCombinedStats(currentHero.getId());
            if (combinedHero != null) {
                this.maxPlayerHp = combinedHero.getHp();
                this.currentPlayerHp = maxPlayerHp; // 升級自動補滿血量
                
                // 同步更新當前 HeroVO 屬性
                currentHero.setLevel(combinedHero.getLevel());
                currentHero.setExp(combinedHero.getExp());
                currentHero.setHp(combinedHero.getHp());
                currentHero.setAtk(combinedHero.getAtk());
                currentHero.setDef(combinedHero.getDef());
                currentHero.setSpeed(combinedHero.getSpeed());
            }
        } else {
            // 如果沒升級，僅在記憶體中更新當前的經驗值
            currentHero.setExp(currentHero.getExp() + currentMonster.getExpReward());
        }

        // 💡 2. 呼叫 BagService 進行隨機掉寶判定與寫入資料庫 (完全解耦！)
        HeroEquipmentVO drop = bagService.rollMonsterLoot(currentHero.getId(), currentMonster);
        if (drop != null) {
            result.addLogMessage("🎁【幸運掉落】" + currentMonster.getName() + " 掉落了 [" + drop.getName() + "] (品級 " + drop.getTier() + ")！已存入您的背包。\n");
        }

        // 3. 生產下一隻怪
        spawnNewMonster();
        if (currentMonster != null) {
            result.addLogMessage("⚔️ 出現了！" + currentMonster.getName() + " (生命值: " + currentMonster.getHp() + ")！\n");
        }
    }

    /**
     * 💡 私有輔助方法：封裝狀態到 DTO 回傳給 UI
     */
    private void fillResultState(BattleTickResult result) {
        result.setPlayerAction(playerActionVal);
        result.setPlayerHp(currentPlayerHp);
        result.setMaxPlayerHp(maxPlayerHp);
        
        if (currentMonster != null) {
            result.setMonsterName(currentMonster.getName() + " Lv." + currentMonster.getLevel());
            result.setMonsterHp(currentMonsterHp);
            result.setMaxMonsterHp(currentMonster.getHp());
            result.setMonsterAction(monsterActionVal);
        } else {
            result.setMonsterName("待機中...");
            result.setMonsterHp(0);
            result.setMaxMonsterHp(0);
            result.setMonsterAction(0.0);
        }
    }
}
