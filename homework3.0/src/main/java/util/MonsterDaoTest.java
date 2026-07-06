package util;

import java.util.List;
import dao.MonsterDao;
import dao.impl.MonsterDaoImpl;
import entity.Monster;

/**
 * 怪物資料存取層單元測試 (MonsterDaoTest)
 * 💡 用來在 Eclipse 中快速測試 `MonsterDaoImpl` 的資料載入功能。
 * 
 * 💡【測試方法】
 *   在 Eclipse 專案總管選中此檔案，右鍵 ➡️ Run As ➡️ Java Application。
 *   觀察 Console 是否正確印出 3 個地圖所載入的怪物明細！
 */
public class MonsterDaoTest {

    public static void main(String[] args) {
        System.out.println("=======================================================");
        System.out.println("          RPG 放置遊戲 - 怪物 DAO 查詢功能測試          ");
        System.out.println("=======================================================");

        MonsterDao monsterDao = new MonsterDaoImpl();

        // 測試 1：查詢新手草原的怪物 (map_id = 1)
        System.out.println("\n[測試 1] 載入 新手草原 (Map ID = 1) 的所有怪物：");
        List<Monster> meadowMonsters = monsterDao.selectByMapId(1);
        printMonsterList(meadowMonsters);

        // 測試 2：查詢幽暗森林的怪物 (map_id = 2)
        System.out.println("\n[測試 2] 載入 幽暗森林 (Map ID = 2) 的所有怪物：");
        List<Monster> forestMonsters = monsterDao.selectByMapId(2);
        printMonsterList(forestMonsters);

        // 測試 3：查詢烈焰火山的怪物 (map_id = 3)
        System.out.println("\n[測試 3] 載入 烈焰火山 (Map ID = 3) 的所有怪物：");
        List<Monster> volcanoMonsters = monsterDao.selectByMapId(3);
        printMonsterList(volcanoMonsters);

        System.out.println("\n=======================================================");
        System.out.println("                      測試結束                          ");
        System.out.println("=======================================================");
    }

    /**
     * 輔助印出怪物清單的方法
     */
    private static void printMonsterList(List<Monster> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("  -> ⚠️ 查無此地圖的怪物資料！");
            return;
        }
        System.out.println("  查詢成功，共找到 " + list.size() + " 隻怪物：");
        for (Monster m : list) {
            System.out.println("  -> " + m.toString());
        }
    }
}
