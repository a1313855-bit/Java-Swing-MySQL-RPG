package entity;

/**
 * 裝備範本實體類別 (Equipment)
 * 💡 對應資料庫中的 `equipment` 表。
 * 代表遊戲中固定的裝備品項（例如 鐵劍 永遠是增加 5 點攻擊力）。
 */
public class Equipment {

    private int id;             // 裝備範本唯一 ID
    private String name;        // 裝備名稱 (例如：新手木劍)
    private String slot;        // 裝備部位 (helmet, armor, weapon, boots)
    private int tier;           // 裝備品級 (1: 基礎, 2: 中級, 3: 高級)
    private int hpBonus;        // 生命值加成
    private int atkBonus;       // 攻擊力加成
    private int defBonus;       // 防禦力加成
    private int speedBonus;     // 速度加成

    /**
     * 💡 無參數建構子
     */
    public Equipment() {
    }

    /**
     * 💡 全參數建構子
     */
    public Equipment(int id, String name, String slot, int tier, int hpBonus, int atkBonus, int defBonus,
            int speedBonus) {
        this.id = id;
        this.name = name;
        this.slot = slot;
        this.tier = tier;
        this.hpBonus = hpBonus;
        this.atkBonus = atkBonus;
        this.defBonus = defBonus;
        this.speedBonus = speedBonus;
    }

    // ==========================================
    // Getter & Setter 方法區
    // ==========================================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public int getHpBonus() {
        return hpBonus;
    }

    public void setHpBonus(int hpBonus) {
        this.hpBonus = hpBonus;
    }

    public int getAtkBonus() {
        return atkBonus;
    }

    public void setAtkBonus(int atkBonus) {
        this.atkBonus = atkBonus;
    }

    public int getDefBonus() {
        return defBonus;
    }

    public void setDefBonus(int defBonus) {
        this.defBonus = defBonus;
    }

    public int getSpeedBonus() {
        return speedBonus;
    }

    public void setSpeedBonus(int speedBonus) {
        this.speedBonus = speedBonus;
    }

    @Override
    public String toString() {
        return "Equipment [id=" + id + ", name=" + name + ", slot=" + slot + ", tier=" + tier + 
               ", hpBonus=" + hpBonus + ", atkBonus=" + atkBonus + 
               ", defBonus=" + defBonus + ", speedBonus=" + speedBonus + "]";
    }
}
