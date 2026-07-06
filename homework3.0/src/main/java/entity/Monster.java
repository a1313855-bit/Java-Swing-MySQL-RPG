package entity;

/**
 * 怪物實體類別 (Monster) 💡 對應資料庫中的 `monster` 表。 用於在掛機戰鬥中，裝載從資料庫讀取出來的怪物能力值與掉寶率設定。
 */
public class Monster {

	private int id; // 怪物的唯一 ID
	private int mapId; // 所屬地圖 ID (1:新手草原, 2:幽暗森林, 3:烈焰火山)
	private String name; // 怪物名稱
	private int level; // 怪物推薦等級
	private int hp; // 怪物生命值上限
	private int atk; // 怪物攻擊力
	private int def; // 怪物防禦力
	private int speed; // 怪物速度 (決定行動值累積速度)
	private int expReward; // 擊敗後給予玩家的經驗值獎勵
	private double dropRate; // 裝備掉落率 (例如 0.05 代表 5% 機率)
	private int lootTier; // 掉落裝備的品級 (1: 基礎, 2: 中級, 3: 高級)

	/**
	 * 💡 無參數建構子 (標準 JavaBean 規範)
	 */
	public Monster() {
	}

	/**
	 * 💡 全參數建構子
	 */
	public Monster(int id, int mapId, String name, int level, int hp, int atk, int def, int speed, int expReward,
			double dropRate, int lootTier) {
		this.id = id;
		this.mapId = mapId;
		this.name = name;
		this.level = level;
		this.hp = hp;
		this.atk = atk;
		this.def = def;
		this.speed = speed;
		this.expReward = expReward;
		this.dropRate = dropRate;
		this.lootTier = lootTier;
	}

	// ==========================================
	// 💡 Getter & Setter 方法區
	// ==========================================

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMapId() {
		return mapId;
	}

	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
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

	public int getExpReward() {
		return expReward;
	}

	public void setExpReward(int expReward) {
		this.expReward = expReward;
	}

	public double getDropRate() {
		return dropRate;
	}

	public void setDropRate(double dropRate) {
		this.dropRate = dropRate;
	}

	public int getLootTier() {
		return lootTier;
	}

	public void setLootTier(int lootTier) {
		this.lootTier = lootTier;
	}

	// ==========================================
	// 💡 toString 方法 (方便測試與 debug 印出)
	// ==========================================
	@Override
	public String toString() {
		return "Monster [id=" + id + ", name=" + name + ", mapId=" + mapId + ", level=" + level + ", hp=" + hp
				+ ", atk=" + atk + ", def=" + def + ", speed=" + speed + ", expReward=" + expReward + ", dropRate="
				+ dropRate + ", lootTier=" + lootTier + "]";
	}
}
