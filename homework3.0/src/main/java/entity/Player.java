package entity;

import java.sql.Timestamp;

/**
 * 玩家帳號實體類別 (Player Entity)
 * 對應資料庫中的 player 資料表。
 * 
 * 💡 命名規則說明：
 * 資料庫欄位使用蛇形命名法（snake_case，如 nick_name），
 * Java 屬性則遵循駝峰命名法（camelCase，如 nickName）。
 */
public class Player {
    // 屬性定義 (與資料庫欄位一一對應)
    private Integer id;             // 玩家唯一 ID (對應 id)
    private String username;        // 登入帳號 (對應 username)
    private String password;        // 登入密碼 (對應 password)
    private String nickName;        // 玩家暱稱 (對應 nick_name)
    private String mail;            // 電子信箱 (對應 mail)
    private Timestamp createTime;   // 註冊時間 (對應 create_time)

    // ==========================================
    // 建構子 (Constructors)
    // ==========================================

    /**
     * 無參數建構子 (預設建構子)
     * 許多框架或反射操作（Reflection）都需要一個無參數建構子。
     */
    public Player() {
    }

    /**
     * 用於「註冊/新增帳號」時使用的建構子
     * 因為此時 ID 還沒由資料庫生成 (Auto Increment)，註冊時間也是由資料庫生成，
     * 所以我們只需要傳入帳號、密碼、暱稱與信箱。
     */
    public Player(String username, String password, String nickName, String mail) {
        this.username = username;
        this.password = password;
        this.nickName = nickName;
        this.mail = mail;
    }

    /**
     * 全參數建構子
     * 當我們從資料庫查詢出完整資料，並要在 Java 中還原成物件時使用。
     */
    public Player(Integer id, String username, String password, String nickName, String mail, Timestamp createTime) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nickName = nickName;
        this.mail = mail;
        this.createTime = createTime;
    }

    // ==========================================
    // Getter & Setter 方法
    // ==========================================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    // ==========================================
    // 輔助方法 (toString)
    // ==========================================

    /**
     * 覆寫 toString 方法，便於未來在 Console 印出物件內容進行 Debug。
     */
    @Override
    public String toString() {
        return "Player {" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='[PROTECTED]'" + // 密碼通常不在日誌中明文顯示
                ", nickName='" + nickName + '\'' +
                ", mail='" + mail + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
