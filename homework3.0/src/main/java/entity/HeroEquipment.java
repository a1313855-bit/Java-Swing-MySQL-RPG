package entity;

/**
 * 角色擁有裝備實體類別 (HeroEquipment)
 * 💡 對應資料庫中的 `hero_equipment` 表。
 * 用於記錄某一隻角色擁有哪些裝備，以及其是否已經穿戴在身上。
 */
public class HeroEquipment {

    private int id;             // 擁有紀錄的唯一流水號 ID
    private int heroId;         // 所屬角色 ID (外鍵)
    private int equipmentId;    // 裝備範本 ID (外鍵)
    private boolean isEquipped; // 是否穿戴中 (true 代表穿在身上, false 代表在背包中)

    /**
     * 💡 無參數建構子
     */
    public HeroEquipment() {
    }

    /**
     * 💡 全參數建構子
     */
    public HeroEquipment(int id, int heroId, int equipmentId, boolean isEquipped) {
        this.id = id;
        this.heroId = heroId;
        this.equipmentId = equipmentId;
        this.isEquipped = isEquipped;
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

    public boolean isEquipped() {
        return isEquipped;
    }

    public void setEquipped(boolean isEquipped) {
        this.isEquipped = isEquipped;
    }

    @Override
    public String toString() {
        return "HeroEquipment [id=" + id + ", heroId=" + heroId + 
               ", equipmentId=" + equipmentId + ", isEquipped=" + isEquipped + "]";
    }
}
