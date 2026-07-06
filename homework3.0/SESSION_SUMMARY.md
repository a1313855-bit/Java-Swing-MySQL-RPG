# RPG 放置遊戲 - 本次會話對話紀錄與學習筆記總結 (SESSION_SUMMARY.md)

💡 **這是一份為您整理的對話紀錄與技術思維總結。您可以將其與專案一併帶去學校，方便隨時複習與回顧我們討論的每一個重要觀念！**

---

## 💬 關鍵對話與學習重點回顧

### 1. 戰鬥系統與 UI 的「關注點分離 (Separation of Concerns)」
*   **討論起因**：我們實作完戰鬥計時器後，您指出：將行動條累加、傷害公式（ATK-DEF）、隨機怪獸選擇等**所有計算與資料庫邏輯都寫在 UI（GameMainUI）裡面，會導致專案混亂**。
*   **業界觀念**：
    *   UI 應該只負責「顯示與監聽點擊」（稱為 Thin View 輕量化介面）。
    *   計算規則屬於「業務邏輯（Business Logic）」，必須移入服務層（Service）。
*   **重構解決方案**：
    *   我們設計了 **`BattleService`** 與 **`BattleServiceImpl`** 來在後台默默計算戰鬥狀態。
    *   引入了 **`BattleTickResult` (DTO)**，每次計時器觸發（0.1秒），Service 計算完後，打包這一袋資料傳回給 UI，UI 再將這些數值更新到畫面上。UI 內不再有任何數學運算！

### 2. 協調型服務 (Orchestration Service) 概念
*   **您的疑惑**：`BattleService` 感覺不需要 `BattleDao`，如果需要那裡面應該是什麼？
*   **業界觀念**：
    *   DAO 的唯一目的是對資料庫進行讀寫（CRUD）。戰鬥只在記憶體中更新進度與狀態，我們不需要一張 `battle` 資料表，因此**完全不需要 `BattleDao`**。
    *   但戰鬥打贏後需要撈怪、需要幫玩家存檔經驗值與等級。
    *   此時，`BattleService` 扮演了**「協調者（Orchestration）」**的角色。它本身不直接對接專屬資料表，而是負責調度並協調 **`MonsterDao`** 與 **`HeroDao`** 來完成這一整套戰鬥流程。這在業界是極度標準的服務設計模式。

### 3. DTO（資料傳輸物件）與 VO（視圖物件）的術語碰撞
*   **您的疑惑**：`BattleTickResult` 聽起來像 VO，但您認知中 VO 存的應該是資料庫的 SQL View（例如 `v_hero_detail`），所以它應該放哪裡？
*   **業界觀念**：
    *   **SQL View (資料庫檢視表)**：是資料庫中的虛擬查詢。
    *   **Java VO (View Object - 視圖物件)**：是 Java 中的概念，**與資料庫 View 完全無關**！它是指「專門為了給 UI 畫面渲染而包裝的物件」。
    *   **Java DTO (Data Transfer Object - 資料傳輸物件)**：負責將多個欄位打包成一個物件，在 Service 與 UI 之間進行傳遞。
    *   在小型或桌面專案中，DTO 與 VO 的界線常會合併。為了職責分明，最標準的寫法是將它們移出到與 `entity` 平行的獨立 package 裡。

### 4. 業界標準根目錄 package 規範
我們在本次對話中，完成了專案目錄的徹底整頓，形成了以下標準的平行解耦結構：
*   `entity`：資料庫實體 Table 對應物件（如 `Hero`、`Monster`、`Equipment`）。
*   `vo`：給 UI 呈現的視圖物件（如 `HeroVO`，與資料庫 SQL 無關）。
*   `dto`：純數據傳輸打包物件（如 `BattleTickResult`）。
*   `service` / `service.impl`：業務邏輯與協調型服務。
*   `dao` / `dao.impl`：資料庫 SQL 讀寫。
*   `controller`：Swing 畫面顯示。

### 5. 背包與裝備的 SQL JOIN 檢視表設計
*   為了查出角色背包裡的裝備名稱、部位、屬性加成，我們選擇了 **INNER JOIN** 將 `hero_equipment` 表與 `equipment` 表拼接。
*   為了在 Java 中保持查詢簡潔，我們在資料庫建立了 **`v_hero_equipment` 檢視表**，並在 Java 的 `vo` 套件下規劃了對應的 **`HeroEquipmentVO`**。

---

## 🗺️ 目前進度與下一步（供明天在學校繼續）

*   **目前狀態**：
    *   資料庫中已成功建立 `v_hero_equipment` 檢視表。
    *   Java 專案中已建立 `vo.HeroEquipmentVO` 視圖物件。
    *   專案編譯 100% 通過。
*   **明天在學校的起步點**：
    *   **設計並實作 `dao.HeroEquipmentDao` 介面與 `dao.impl.HeroEquipmentDaoImpl` 類別**。
    *   我們需要宣告的三個核心方法為：
        1.  `List<HeroEquipmentVO> selectByHeroId(int heroId);` (查詢角色背包裝備)
        2.  `boolean insert(entity.HeroEquipment heroEquip);` (掉寶時寫入新裝備)
        3.  `boolean updateEquippedStatus(int id, boolean isEquipped);` (穿戴/脫下裝備狀態修改)
