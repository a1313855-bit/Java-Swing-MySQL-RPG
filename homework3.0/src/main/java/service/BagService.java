package service;

import java.util.List;
import vo.HeroEquipmentVO;
import entity.Monster;

/**
 * 背包系統業務邏輯介面 (BagService)
 * 💡【軟體架構學習筆記：業務層與資料庫層命名解耦】
 *   - 雖然資料庫表格名為 `hero_equipment`，DAO 名為 `HeroEquipmentDao`，
 *     但在業務邏輯層 (Service)，我們將其命名為符合玩家認知與功能名稱的 `BagService` (背包服務)。
 *   - 本服務負責協調裝備的讀取、穿戴、脫下，並在穿戴時執行「同一部位限穿一件」的遊戲規則。
 */
public interface BagService {

    /**
     * 讀取某個角色背包裡的所有裝備列表 (已穿戴 + 未穿戴)
     * @param heroId 角色 ID
     * @return List<HeroEquipmentVO> 裝備視圖資料清單
     */
    List<HeroEquipmentVO> getBagItems(int heroId);

    /**
     * 切換裝備的穿戴狀態
     * 💡 核心遊戲規則：
     *   - 若該裝備為「背包中」，點擊後轉為「已穿戴」：
     *     Service 會自動檢查該部位 (Slot) 是否已有其他裝備被穿戴。若有，會自動將其「脫下」，再「穿上」新裝備。
     *   - 若該裝備為「已穿戴」，點擊後轉為「背包中」(脫下)。
     * 
     * @param heroEquipmentId 該背包裝備的唯一流水號 ID (hero_equipment.id)
     * @param heroId 角色 ID
     * @return boolean 是否切換成功
     */
    boolean toggleEquipStatus(int heroEquipmentId, int heroId);

    /**
     * 處理怪物戰敗時的隨機掉寶邏輯，並存入背包
     * 💡 核心遊戲規則：
     *   - 根據怪物的掉寶率 (Drop Rate) 判定是否中獎。
     *   - 若中獎，根據怪物所屬的掉寶品級 (Loot Tier)，隨機抽取一個裝備範本寫入玩家背包中。
     * 
     * @param heroId 角色 ID
     * @param monster 戰敗的怪物實體
     * @return HeroEquipmentVO 掉落的裝備細節 (若無掉落則回傳 null)
     */
    HeroEquipmentVO rollMonsterLoot(int heroId, Monster monster);
}
