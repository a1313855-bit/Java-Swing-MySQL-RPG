package dto;

/**
 * 角色升級判定結果資料傳輸物件 (LevelUpResult)
 * 💡 用於在 HeroStatsService 與 BattleService 之間傳遞升級狀態與數據。
 */
public class LevelUpResult {

    private boolean leveledUp; // 是否升級
    private int oldLevel;      // 升級前等級
    private int newLevel;      // 升級後等級

    public LevelUpResult() {
        this.leveledUp = false;
    }

    public LevelUpResult(boolean leveledUp, int oldLevel, int newLevel) {
        this.leveledUp = leveledUp;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    public boolean isLeveledUp() {
        return leveledUp;
    }

    public void setLeveledUp(boolean leveledUp) {
        this.leveledUp = leveledUp;
    }

    public int getOldLevel() {
        return oldLevel;
    }

    public void setOldLevel(int oldLevel) {
        this.oldLevel = oldLevel;
    }

    public int getNewLevel() {
        return newLevel;
    }

    public void setNewLevel(int newLevel) {
        this.newLevel = newLevel;
    }
}
