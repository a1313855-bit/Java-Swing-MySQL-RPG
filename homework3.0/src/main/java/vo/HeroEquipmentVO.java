package vo;

/**
 * 角色背包裝備檢視視圖物件 (HeroEquipmentVO - View Object)
 * 💡【軟體架構學習筆記：VO (View Object)】
 *   - 此類別專門映射 MySQL 檢視表 `v_hero_equipment` 的查詢結果。
 *   - 它負責將「角色背包中的裝備實體 (hero_equipment)」與「裝備基本能力範本 (equipment)」結合的數據，
 *     一次性傳遞給 UI (BagUI) 來進行繪製，避免了效能低下的 N+1 查詢。
 */
public class HeroEquipmentVO {

    private int id;             // 背包裝備唯一的流水號 ID (對應 hero_equipment.id)
    private int heroId;         // 擁有該裝備的角色 ID
    private int equipmentId;    // 裝備範本 ID
    private boolean isEquipped; // 是否穿戴中 (true:穿戴在身上, false:在背包中)
    private String name;        // 裝備名稱 (如：精鋼大劍)
    private String slot;        // 裝備部位 (helmet, armor, weapon, boots)
    private int tier;           // 裝備品級 (1: 基礎, 2: 中級, 3: 高級)
    private int hpBonus;        // 生命加成
    private int atkBonus;       // 攻擊加成
    private int defBonus;       // 防禦加成
    private int speedBonus;     // 速度加成

    /**
     * 💡 無參數建構子 (JavaBean 規範)
     */
    public HeroEquipmentVO() {
    }

    /**
     * 💡 全參數建構子
     */
    public HeroEquipmentVO(int id, int heroId, int equipmentId, boolean isEquipped, String name, String slot, int tier,
            int hpBonus, int atkBonus, int defBonus, int speedBonus) {
        this.id = id;
        this.heroId = heroId;
        this.equipmentId = equipmentId;
        this.isEquipped = isEquipped;
        this.name = name;
        this.slot = slot;
        this.tier = tier;
        this.hpBonus = hpBonus;
        this.atkBonus = atkBonus;
        this.defBonus = defBonus;
        this.speedBonus = speedBonus;
    }

    // =========================================================================
    // 💡 Getter & Setter 方法區
    // =========================================================================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHeroId() {
        return heroId;
    }

    public void setHeroId(int heroId) {
        this.heroId = heroId;
    }

    public int getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
    }

    /**
     * 💡【學習筆記：Boolean 型態的 Getter 命名規範】
     *   在 Java 標準（JavaBeans 規範）中，
     *   - 一般型態（如 int, String）的 Getter 以 `get` 開頭 (如 `getId()`, `getName()`)。
     *   - 布林值（boolean）型態的 Getter 以 `is` 開頭 (如 `isEquipped()`)。
     */
    public boolean isEquipped() {
        return isEquipped;
    }

    public void setEquipped(boolean isEquipped) {
        this.isEquipped = isEquipped;
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
        return "HeroEquipmentVO [id=" + id + ", heroId=" + heroId + ", equipmentId=" + equipmentId + 
               ", isEquipped=" + isEquipped + ", name=" + name + ", slot=" + slot + ", tier=" + tier + 
               ", hpBonus=" + hpBonus + ", atkBonus=" + atkBonus + ", defBonus=" + defBonus + 
               ", speedBonus=" + speedBonus + "]";
    }
}
