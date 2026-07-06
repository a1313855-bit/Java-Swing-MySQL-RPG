package service;

import vo.HeroVO;
import dto.LevelUpResult;

/**
 * 角色屬性數值服務介面 (HeroStatsService)
 * 💡【軟體架構學習筆記：專屬功能服務抽離】
 *   - 這是您提出的優秀設計！我們將「與角色數值變動、戰力加成計算」有關的邏輯，
 *     獨立成一個專屬的服務層，而不與選角大廳的 HeroService 混在一起。
 *   - 目前包含的核心職責：計算「基礎屬性 + 已穿戴裝備加成」後的總合屬性。
 */
public interface HeroStatsService {

    /**
     * 獲取角色結合了已穿戴裝備屬性加成後的「最終總合數值」
     * @param heroId 角色 ID
     * @return HeroVO 裝有最終攻擊、防禦、生命值與攻速的視圖物件
     */
    HeroVO getCombinedStats(int heroId);

    /**
     * 增加角色經驗值，並進行升級判定與存檔
     * 💡 核心遊戲規則：
     *   - 每次打敗怪物，角色獲得 EXP。
     *   - 若當前經驗值超過「當前等級 * 100」，則觸發等級提升。
     *   - 升級時，基礎屬性上限提升：血量上限增加 15、攻擊力 +1、防禦力 +1、速度 +1，並保存至資料庫。
     * 
     * @param heroId 角色 ID
     * @param expGained 獲得的經驗值
     * @return LevelUpResult 升級結果 DTO
     */
    LevelUpResult addExpAndCheckLevelUp(int heroId, int expGained);

    /**
     * 手動分配角色未分配屬性點 (自由配點)
     * 💡 核心遊戲規則：
     *   - 生命加點：1 點自由屬性點 ➡️ 增加 15 點基礎 HP 上限。
     *   - 攻擊加點：1 點自由屬性點 ➡️ 增加 1 點基礎 ATK。
     *   - 防禦加點：1 點自由屬性點 ➡️ 增加 1 點基礎 DEF。
     *   - 速度加點：1 點自由屬性點 ➡️ 增加 1 點基礎 SPEED。
     *   - 本服務會自動驗證加點點數總和是否小於或等於角色的 stat_points 剩餘點數。
     * 
     * @param heroId 角色 ID
     * @param hpAlloc 分配給生命值的點數
     * @param atkAlloc 分配給攻擊力的點數
     * @param defAlloc 分配給防禦力的點數
     * @param speedAlloc 分配給速度的點數
     * @return boolean 是否分配並存檔成功
     */
    boolean allocateStatPoints(int heroId, int hpAlloc, int atkAlloc, int defAlloc, int speedAlloc);
}
