package util;

import dao.HeroDao;
import dao.impl.HeroDaoImpl;
import entity.Hero;
import entity.HeroStats;
import vo.HeroVO;

import java.util.List;

/**
 * 角色資料存取層測試程式 (HeroDaoTest) 用於驗證我們的多表交易寫入 (Transaction) 與連帶刪除 (Cascade Delete)
 * 邏輯是否運作正常。
 */
public class HeroDaoTest {

	public static void main(String[] args) {
		HeroDao heroDao = new HeroDaoImpl();

		System.out.println("=======================================================");
		System.out.println("            RPG 放置遊戲 - 角色 DAO 整合功能測試          ");
		System.out.println("=======================================================");

		// -----------------------------------------------------
		// 測試 1：查詢玩家的所有角色 (使用 playerId = 1，對應 testuser)
		// -----------------------------------------------------
		System.out.println("\n[測試 1] 查詢玩家 ID = 1 (testuser) 的所有角色明細...");
		List<HeroVO> initialHeroes = heroDao.selectByPlayerId(1);
		System.out.println("查詢結果共：" + initialHeroes.size() + " 個角色：");
		for (HeroVO h : initialHeroes) {
			System.out.println("  -> " + h);
		}

		// -----------------------------------------------------
		// 測試 2：新增角色 (使用 Transaction 交易機制)
		// -----------------------------------------------------
		System.out.println("\n[測試 2] 新增一個新角色「小精靈」 (測試 Transaction 是否成功)...");

		// 建立基本資料 (不含 id，由 MySQL 自動生成)
		Hero newHero = new Hero(1, "小精靈", "女性", "female_hero.jpg");

		// 建立戰鬥屬性 (heroId 將在 insert 交易中取得)
		HeroStats initialStats = new HeroStats();
		initialStats.setLevel(1);
		initialStats.setExp(0);
		initialStats.setAtk(12);
		initialStats.setDef(4);
		initialStats.setSpeed(18);

		boolean insertResult = heroDao.insert(newHero, initialStats);
		System.out.println("新增結果：" + (insertResult ? "【成功】" : "【失敗】"));

		// -----------------------------------------------------
		// 測試 3：再次查詢，確認「小精靈」是否在兩張表中都新增成功，並能透過 View 讀出
		// -----------------------------------------------------
		System.out.println("\n[測試 3] 再次查詢玩家 ID = 1 的角色列表，確認是否成功加入...");
		List<HeroVO> updatedHeroes = heroDao.selectByPlayerId(1);
		int elfId = -1;
		for (HeroVO h : updatedHeroes) {
			System.out.println("  -> " + h);
			if ("小精靈".equals(h.getName())) {
				elfId = h.getId(); // 記錄小精靈的角色 ID，用於等一下的刪除測試
			}
		}

		// -----------------------------------------------------
		// 測試 4：刪除角色 (測試 ON DELETE CASCADE)
		// -----------------------------------------------------
		if (elfId != -1) {
			System.out.println("\n[測試 4] 刪除剛剛建立的角色「小精靈」(ID = " + elfId + ")...");
			System.out.println("💡 說明：我們只對 hero 表進行 delete，因外鍵設定，hero_stats 中的資料會被資料庫一併自動刪除！");

			boolean deleteResult = heroDao.deleteById(elfId);
			System.out.println("刪除結果：" + (deleteResult ? "【成功】" : "【失敗】"));

			// -----------------------------------------------------
			// 測試 5：驗證是否真的刪除乾淨
			// -----------------------------------------------------
			System.out.println("\n[測試 5] 最終驗證玩家 ID = 1 的角色列表...");
			List<HeroVO> finalHeroes = heroDao.selectByPlayerId(1);
			System.out.println("最終角色清單剩餘：" + finalHeroes.size() + " 個角色：");
			for (HeroVO h : finalHeroes) {
				System.out.println("  -> " + h);
			}
		} else {
			System.out.println("\n[警告] 未能成功取得新創角色的 ID，跳過刪除測試。");
		}

		System.out.println("\n=======================================================");
		System.out.println("                    測試結束！                         ");
		System.out.println("=======================================================");
	}
}
