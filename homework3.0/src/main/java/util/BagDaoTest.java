package util;

import java.util.List;

import dao.HeroEquipmentDao;
import dao.impl.HeroEquipmentDaoImpl;
import entity.HeroEquipment;
import vo.HeroEquipmentVO;

/**
 * 背包與裝備資料存取層單元測試 (BagDaoTest)
 * 💡【單元測試原理】
 *   在 UI 畫面寫好之前，我們先用這個小程式在後台對資料庫進行測試。
 *   測試包含：
 *     1. 模擬怪物掉寶寫入資料庫 (`insert`)。
 *     2. 模擬讀取背包列表 (`selectByHeroId`)。
 *     3. 模擬穿上裝備 (`updateEquippedStatus`)。
 * 
 * 💡【測試方法】
 *   在 Eclipse 選中此檔案，右鍵 ➡️ Run As ➡️ Java Application。
 *   觀察 Console 是否印出正確的裝備名稱與狀態！
 */
public class BagDaoTest {

    public static void main(String[] args) {
        System.out.println("=======================================================");
        System.out.println("          RPG 放置遊戲 - 背包 DAO 關聯功能測試          ");
        System.out.println("=======================================================");

        HeroEquipmentDao bagDao = new HeroEquipmentDaoImpl();
        
        // 💡 假設我們要測試的角色 ID 是 1 (資料庫中預設為亞瑟)
        int testHeroId = 1;

        // 測試 1：先查背包 (目前應該是空的，或者只有您先前塞的資料)
        System.out.println("\n[步驟 1] 讀取亞瑟目前的背包清單：");
        List<HeroEquipmentVO> originalBag = bagDao.selectByHeroId(testHeroId);
        printBagList(originalBag);

        // 測試 2：模擬怪物掉落兩件裝備，並塞進背包
        System.out.println("\n[步驟 2] 模擬掉落裝備並塞入背包...");
        // 掉落新手木劍 (Template ID = 1)
        HeroEquipment item1 = new HeroEquipment();
        item1.setHeroId(testHeroId);
        item1.setEquipmentId(1);
        item1.setEquipped(false); // 預設進背包，不穿戴
        
        // 掉落皮革帽 (Template ID = 2)
        HeroEquipment item2 = new HeroEquipment();
        item2.setHeroId(testHeroId);
        item2.setEquipmentId(2);
        item2.setEquipped(false); // 預設進背包，不穿戴
        
        boolean insert1 = bagDao.insert(item1);
        boolean insert2 = bagDao.insert(item2);
        
        System.out.println("  -> 新手木劍寫入結果: " + (insert1 ? "成功" : "失敗"));
        System.out.println("  -> 皮革帽寫入結果: " + (insert2 ? "成功" : "失敗"));

        // 測試 3：再次查詢背包，確認 JOIN 檢視表是否有抓出裝備細節 (名字、加成)
        System.out.println("\n[步驟 3] 再次讀取亞瑟的背包清單：");
        List<HeroEquipmentVO> updatedBag = bagDao.selectByHeroId(testHeroId);
        printBagList(updatedBag);

        // 測試 4：模擬玩家「穿上」剛剛掉落的新手木劍
        if (updatedBag != null && !updatedBag.isEmpty()) {
            // 找到剛寫入的新手木劍 (名字叫 "新手木劍" 的那個 id)
            int woodSwordRelationId = -1;
            for (HeroEquipmentVO vo : updatedBag) {
                if ("新手木劍".equals(vo.getName())) {
                    woodSwordRelationId = vo.getId();
                    break;
                }
            }

            if (woodSwordRelationId != -1) {
                System.out.println("\n[步驟 4] 模擬玩家點擊「穿上新手木劍」(ID = " + woodSwordRelationId + ")...");
                boolean equipResult = bagDao.updateEquippedStatus(woodSwordRelationId, true);
                System.out.println("  -> 裝備穿戴更新結果: " + (equipResult ? "成功" : "失敗"));

                // 驗證狀態是否真的改為「已裝備」
                System.out.println("\n[步驟 5] 最後驗證背包狀態：");
                List<HeroEquipmentVO> finalBag = bagDao.selectByHeroId(testHeroId);
                printBagList(finalBag);

                // 💡 步驟 6：驗證角色加成後的總合屬性計算 (結合 DAO 與 Service)
                System.out.println("\n[步驟 6] 呼叫 HeroStatsService 驗證亞瑟屬性加成計算：");
                service.HeroStatsService statsService = new service.impl.HeroStatsServiceImpl();
                vo.HeroVO combinedHero = statsService.getCombinedStats(testHeroId);
                if (combinedHero != null) {
                    System.out.println("  -> 亞瑟加成後最終戰鬥屬性：");
                    System.out.println("     - 生命值: " + combinedHero.getHp() + " (包含皮革帽 HP+15)");
                    System.out.println("     - 攻擊力: " + combinedHero.getAtk() + " (包含新手木劍 ATK+2)");
                    System.out.println("     - 防禦力: " + combinedHero.getDef());
                    System.out.println("     - 速度 (攻速): " + combinedHero.getSpeed());
                } else {
                    System.err.println("  -> ❌ 無法取得加成後屬性，查詢結果為 null");
                }
            }
        }

        System.out.println("\n=======================================================");
        System.out.println("                      測試結束                          ");
        System.out.println("=======================================================");
    }

    /**
     * 輔助印出背包列表的方法
     */
    private static void printBagList(List<HeroEquipmentVO> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("  -> 🎒 背包是空的！裡面沒有任何裝備。");
            return;
        }
        System.out.println("  共找到 " + list.size() + " 件裝備：");
        for (HeroEquipmentVO vo : list) {
            String status = vo.isEquipped() ? "【已穿戴】" : "【背包中】";
            System.out.println("  -> ID: " + vo.getId() + " | " + vo.getName() + " (" + vo.getSlot() + ") " +
                               "| 加成: ATK+" + vo.getAtkBonus() + ", HP+" + vo.getHpBonus() + 
                               " | 狀態: " + status);
        }
    }
}
