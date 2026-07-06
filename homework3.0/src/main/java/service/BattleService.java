package service;

import vo.HeroVO;
import dto.BattleTickResult;

/**
 * 💡【軟體架構學習筆記：協調型服務 (Orchestration Service)】
 * 
 * ❓ 什麼是「協調型服務」？
 *   在標準的三層架構中，我們習慣一個 Service 對接一個 DAO（例如 HeroService 對應 HeroDao）。
 *   但當我們遇到「戰鬥系統」這種「純邏輯、無專屬資料表、有狀態」的複雜規則時，就不會有對應的「BattleDao」了。
 *   此時，我們建立的 `BattleService` 即為一個「協調型服務」：
 *     1. 它本身沒有專屬的 `battle` 資料表，因此不需要 `BattleDao`。
 *     2. 它負責「協調並調度」其他多個資料存取層（例如 MonsterDao 來撈取怪獸、HeroDao 來更新玩家等級/EXP）。
 *     3. 它負責管理戰鬥在記憶體中的狀態（生命值、行動值），將業務邏輯（如傷害公式 ATK-DEF）徹底封裝。
 * 
 * ❓ 它與工具類別 (Util) 有何不同？
 *   - Util 是「無狀態的 (Stateless)」：只提供純靜態方法，計算完即走，不在記憶體中保留任何數據。
 *   - 此 Service 是「有狀態的 (Stateful)」：必須持續儲存並累加玩家與怪物的行動值，掌控整個戰鬥迴圈。
 */
public interface BattleService {

    /**
     * 啟動掛機戰鬥，進行狀態初始化
     * @param hero 當前選定的角色 VO
     * @param mapId 當前選擇探索的地圖 ID (1:草原, 2:森林, 3:火山)
     */
    void startBattle(HeroVO hero, int mapId);

    /**
     * 執行一次戰鬥心跳 (Tick) 運算
     * 💡 由 UI 的 Timer 每 100 毫秒呼叫一次。
     * @return BattleTickResult 本次運算後的結果封裝，供 UI 刷新畫面使用
     */
    BattleTickResult tick();

    /**
     * 強制停止掛機戰鬥，重置所有戰鬥變數狀態
     */
    void stopBattle();
}
