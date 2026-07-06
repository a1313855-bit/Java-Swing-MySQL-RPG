package vo;

/**
 * 角色整合資訊展示物件 (HeroVO - View Object) 💡【軟體架構學習筆記：視圖物件 (View Object)】 -
 * 本類別已移動到最外層的 `vo` 套件中，實現了與 `entity`（資料庫實體）平行解耦的乾淨架構。 - 它雖然讀取自 MySQL 的
 * `v_hero_detail` 檢視表，但在 Java 專案結構中，它是一個標準的 View Object (VO)，
 * 用來把多個表格合併的資料，乾淨地呈現給使用者選角大廳與遊戲主介面。
 */
public class HeroVO {

	private int id; // 角色唯一 ID
	private int playerId; // 所屬玩家帳號 ID
	private String name; // 角色暱稱
	private String gender; // 性別
	private String imgName; // 角色頭像圖片檔名
	private int level; // 等級
	private int exp; // 經驗值
	private int hp; // 生命值 (生命點數)
	private int atk; // 攻擊力
	private int def; // 防禦力
	private int speed; // 速度
	private int statPoints; // 未分配屬性點 (自由配點)

	// 無參數建構子
	public HeroVO() {
	}

	public HeroVO(int id, int playerId, String name, String gender, String imgName, int level, int exp, int hp, int atk,
			int def, int speed) {
		this.id = id;
		this.playerId = playerId;
		this.name = name;
		this.gender = gender;
		this.imgName = imgName;
		this.level = level;
		this.exp = exp;
		this.hp = hp;
		this.atk = atk;
		this.def = def;
		this.speed = speed;
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

	public int getStatPoints() {
		return statPoints;
	}

	public void setStatPoints(int statPoints) {
		this.statPoints = statPoints;
	}

	// toString
	@Override
	public String toString() {
		return "HeroVO [id=" + id + ", playerId=" + playerId + ", name=" + name + ", gender=" + gender + ", imgName="
				+ imgName + ", level=" + level + ", exp=" + exp + ", hp=" + hp + ", atk=" + atk + ", def=" + def
				+ ", speed=" + speed + "]";
	}
}
