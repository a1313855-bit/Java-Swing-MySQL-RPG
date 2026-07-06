package entity;

/**
 * 角色基本資訊實體類別 (Hero)
 * 對應資料庫中的 hero 表。
 */
public class Hero {

    private int id;          // 角色唯一的識別 ID (主鍵)
    private int playerId;    // 所屬的玩家帳號 ID (外鍵)
    private String name;     // 角色名稱 (唯一值)
    private String gender;   // 性別
    private String imgName;  // 像素頭像圖片檔名 (預備作為造型 skins 使用)

    // 無參數建構子 (JPA 或一些框架常用，好習慣)
    public Hero() {
    }

    // 全參數建構子 (建立物件時方便賦值)
    public Hero(int id, int playerId, String name, String gender, String imgName) {
        this.id = id;
        this.playerId = playerId;
        this.name = name;
        this.gender = gender;
        this.imgName = imgName;
    }

    // 常用建構子 (不包含自動遞增的 id，因為寫入資料庫前還沒有 id)
    public Hero(int playerId, String name, String gender, String imgName) {
        this.playerId = playerId;
        this.name = name;
        this.gender = gender;
        this.imgName = imgName;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    // toString 方法，方便 System.out.println() 印出測試資料
    @Override
    public String toString() {
        return "Hero [id=" + id + ", playerId=" + playerId + ", name=" + name + ", gender=" + gender + ", imgName="
                + imgName + "]";
    }
}
