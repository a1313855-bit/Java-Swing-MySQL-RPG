package dao;

import entity.Hero;
import entity.HeroStats;
import vo.HeroVO;
import java.util.List;

/**
 * 角色資料存取介面 (HeroDao)
 * 
 * 💡【複習與學習筆記】
 * 
 * 1. 關於「讀取該玩家所有角色」：
 *    - 為什麼回傳 List<HeroVO>？
 *      因為一個玩家可以創 0 到 5 個角色，數量是不固定的，因此用 Java 的動態陣列 List 最合適。
 *      我們查詢的是 v_hero_detail (View)，所以裝載的物件是融合了基本資料與戰鬥數值的 HeroVO。
 * 
 * 2. 關於「刪除角色 (deleteById)」：
 *    - 💡【我曾寫錯的觀念】我原本想說要在 Service 中手動呼叫「刪除基本資料」與「刪除戰鬥數值」兩個方法。
 *    - 💡【正確觀念與原因】我們在 MySQL 建立 `hero_stats` 表時，設定了外鍵約束且加上了 `ON DELETE CASCADE` (級聯刪除)。
 *      這代表只要我們對 `hero` 表下指令刪除該角色，MySQL 引擎就會「自動且瞬間」刪除 `hero_stats` 裡對應的數值！
 *      因此，Java 端只需要「刪除基本表的一筆資料」即可，不用寫兩次刪除。這減少了程式碼也防止漏刪。
 * 
 * 3. 關於「創建新角色 (insert)」：
 *    - 💡【資料安全挑戰】創建角色要寫入兩張表：`hero` (基本資料) 與 `hero_stats` (戰鬥數值)。
 *      如果 `hero` 新增成功，但寫入 `hero_stats` 時電腦斷電或出錯，資料庫就會產生「只有名字，沒有戰鬥數值」的壞掉角色，導致遊戲當機。
 *    - 💡【解決方案：交易機制 (Transaction)】
 *      在 JDBC 實作中，我們會關閉自動提交，讓這兩個新增動作視為同一個「原子單位（要麼全成功，要麼全失敗）」。
 *      若中途有任何一個失敗，就進行「Rollback (回滾)」，讓 MySQL 吐出已寫入的資料，確保資料庫完美無瑕！
 * 
 * 4. 關於「修改角色名稱與造型」：
 *    - 💡【我曾寫錯的觀念】我原本寫：`Hero updateByIdSetName(int id)`，這有兩個問題：
 *      ① 忘記傳入「要修改成什麼新名字（String newName）」，方法沒有參數就無法執行修改。
 *      ② 回傳型態寫成 `Hero`，但 Update 動作通常不需要回傳整個物件，只要回傳 `boolean`（告訴程式修改成功或失敗）或 `int`（影響行數）即可。
 *    - 💡【正確觀念與原因】標準宣告應包含「目標ID」與「新值參數」，回傳型態為 `boolean`。
 */
public interface HeroDao {

    /**
     * 讀取特定玩家帳號下的所有角色資訊 (從檢視表 v_hero_detail 讀取)
     * @param playerId 玩家帳號唯一的識別 ID (用於鎖定是哪位玩家)
     * @return List<HeroVO> 該玩家擁有的所有角色整合資料清單
     */
    List<HeroVO> selectByPlayerId(int playerId);

    /**
     * 創建新角色 (同時寫入 hero 基本資料與 hero_stats 戰鬥數值，使用 Transaction 交易機制)
     * @param hero 要新增的角色基本資訊 (不含自動產生的 id)
     * @param stats 要新增的角色初始數值 (其 heroId 將在寫入時與 hero.id 綁定)
     * @return boolean 是否兩張表都順利創建成功
     */
    boolean insert(Hero hero, HeroStats stats);

    /**
     * 刪除角色 (僅需刪除 hero 基本表，MySQL 會自動 CASCADE 刪除對應的 stats)
     * @param heroId 準備被刪除的角色唯一 ID
     * @return boolean 是否刪除成功
     */
    boolean deleteById(int heroId);

    /**
     * 修改角色名稱
     * 💡【學習點】必須傳入 newName 參數，回傳 boolean 代表修改成功/失敗
     * @param heroId 角色唯一 ID
     * @param newName 新的角色名稱
     * @return boolean 是否修改成功
     */
    boolean updateName(int heroId, String newName);

    /**
     * 修改角色顯示圖片 (換造型)
     * 💡【學習點】必須傳入 newImgName 參數，回傳 boolean 代表修改成功/失敗
     * @param heroId 角色唯一 ID
     * @param newImgName 新的圖片檔名 (例如: new_skin.jpg)
     * @return boolean 是否修改成功
     */
    boolean updateImgName(int heroId, String newImgName);

    /**
     * 根據玩家 ID 與角色名稱查詢特定角色明細 (用於創建角色時的重複名稱檢查)
     * @param playerId 玩家 ID
     * @param heroName 角色名稱
     * @return List<HeroVO> 查詢到的角色明細列表 (若無符合資料則為空列表)
     */
    List<HeroVO> selectByPlayerIdAndHeroName(int playerId, String heroName);

    /**
     * 更新指定角色的等級與經驗值 (用於戰鬥勝利升級存檔)
     * @param heroId 角色 ID
     * @param level 新的等級
     * @param exp 新的經驗值
     * @return boolean 是否更新成功
     */
    boolean updateLevelAndExp(int heroId, int level, int exp);

    /**
     * 根據角色 ID 查詢單個角色詳細屬性資訊 (從 v_hero_detail 檢視表讀取)
     * @param heroId 角色 ID
     * @return HeroVO 角色視圖資料 (若不存在則回傳 null)
     */
    HeroVO selectByHeroId(int heroId);

    /**
     * 更新指定角色的所有基礎屬性與等級經驗 (用於升級、分配點數後存檔)
     * 💡 寫入 `hero_stats` 表
     * @param hero 角色屬性資料
     * @return boolean 是否更新成功
     */
    boolean updateStats(HeroVO hero);
}
