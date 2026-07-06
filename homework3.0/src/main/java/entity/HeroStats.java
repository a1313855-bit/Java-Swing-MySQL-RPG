package entity;

/**
 * 角色戰鬥數值實體類別 (HeroStats)
 * 對應資料庫中的 hero_stats 表，與 hero 表形成一對一關係。
 */
public class HeroStats {

    private int heroId;       // 所屬角色 ID (主鍵兼外鍵)
    private int level;        // 等級
    private int exp;          // 經驗值
    private int hp = 100;     // 生命值 (生命點數，預設為 100)
    private int atk;          // 攻擊力
    private int def;          // 防禦力
    private int speed;        // 速度

    // 無參數建構子
    public HeroStats() {
    }

    // 全參數建構子
    public HeroStats(int heroId, int level, int exp, int hp, int atk, int def, int speed) {
        this.heroId = heroId;
        this.level = level;
        this.exp = exp;
        this.hp = hp;
        this.atk = atk;
        this.def = def;
        this.speed = speed;
    }

    // Getters and Setters
    public int getHeroId() {
        return heroId;
    }

    public void setHeroId(int heroId) {
        this.heroId = heroId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getAtk() {
        return atk;
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }

    public int getDef() {
        return def;
    }

    public void setDef(int def) {
        this.def = def;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }



    // toString
    @Override
    public String toString() {
        return "HeroStats [heroId=" + heroId + ", level=" + level + ", exp=" + exp + ", hp=" + hp + ", atk=" + atk + ", def=" + def
                + ", speed=" + speed + "]";
    }
}
