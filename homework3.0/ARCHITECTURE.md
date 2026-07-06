# RPG 放置遊戲 - 專案架構與目錄規範說明書 (ARCHITECTURE.md)

💡 **親愛的玩家與開發者，這是一份專為本專案打造的「架構學習與開發規範手冊」。**
本專案採用業界最經典的**分層架構（Layered Architecture）**，並引入了 **MVC (Model-View-Controller)** 與 **關注點分離 (Separation of Concerns)** 的核心概念。

以下為每個資料夾（Package）存在的意義、職責劃分，以及開發新功能時的「歸類指南」。

---

## 📂 專案目錄結構樹與職責說明

```text
D:\work\homework3.0\src\main\java\
├── controller\         # 視窗介面 (View / Presentation Layer)
├── dao\                # 資料存取介面 (Data Access Object - Interface)
├── dao.impl\           # 資料存取實作 (JDBC SQL Implementations)
├── dto\                # 資料傳輸物件 (Data Transfer Object - Pure Data DTO)
├── entity\             # 資料庫實體 (Database Table-Mapped Objects)
├── vo\                 # 視圖展示物件 (View Object - UI Presentation VO)
├── service\            # 業務邏輯服務層介面 (Business Outline Interfaces)
├── service.impl\       # 服務層實作 (Business & Orchestration Implementations)
└── util\               # 系統工具與測試類別 (Utility & Test Classes)
```

---

## 🛠️ 各資料夾（Package）詳細職責

### 1. `controller` (視窗介面層)
*   **職責**：負責 Swing GUI 的元件版面繪製（JFrame, JPanel, JButton...）、綁定按鈕點擊事件，以及讀取資料來「畫」在畫面上。
*   **原則**：**UI 必須保持「愚蠢（Thin View）」**。UI 絕不包含任何傷害公式、計時器累加、或 SQL 語句。UI 只當傳話筒，呼叫 Service 層拿資料，拿到資料就直接塞給 Swing 元件。
*   **範例**：`GameMainUI.java` (主遊戲視窗), `LoginFrame.java` (登入視窗)。

### 2. `dao` (資料存取介面) 與 `dao.impl` (資料存取實作)
*   **職責**：唯一負責**「與 MySQL 資料庫打交道」**的地方。
    *   `dao`：宣告大綱，定義我們要對資料庫做哪些查詢與寫入。
    *   `dao.impl`：使用 JDBC 語法，撰寫具體的 SQL 語句、開啟 PreparedStatement、讀取 ResultSet，並防呆釋放 Conn/Statement 資源。
*   **原則**：**不可包含業務邏輯**。例如，DAO 只負責把角色的等級從 5 改成 6，但「角色要拿到多少經驗才能升級」這項規則，DAO 絕對不管（那是 Service 的職責）。
*   **範例**：`HeroDao.java` (介面), `HeroDaoImpl.java` (實作)。

### 3. `entity` (資料庫實體)
*   **職責**：與 MySQL 中的**實體資料表（Physical Tables）一對一映射**的乾淨 Java 物件。
*   **原則**：資料庫裡有幾張 Table，這裡就有幾個對應的 Java 類別。每個類別的欄位名稱、資料型態，必須跟資料庫完全一致。
*   **範例**：`Hero.java` (映射 `hero` 表), `Monster.java` (映射 `monster` 表), `Equipment.java` (映射 `equipment` 表)。

### 4. `dto` (資料傳輸物件 - Data Transfer Object)
*   **職責**：專門用來**「跨層級打包資料」**的臨時容器。它沒有對應資料庫表格，也不會被存進資料庫。
*   **原則**：當 Service 層運算出一堆複雜的零散數據，沒辦法用一個簡單的 int 或 String 傳回給 UI 時，我們就寫一個 DTO 把這些數據「打包成一整袋」一次傳過去。它只有變數和 Getter/Setter，沒有業務邏輯。
*   **範例**：`BattleTickResult.java` (打包每一次戰鬥心跳產生的血量、行動值、對白日誌)。

### 5. `vo` (視圖展示物件 - View Object)
*   **職責**：專門為了**「UI 畫面呈現」**而量身打造的整合型資料物件。
*   **原則**：很多時候 UI 畫面需要的資訊，在資料庫裡是分散在好幾張表中的（例如需要角色的名字、又需要他的力量、體力等數值）。我們在 `vo` 包中建立一個專為該畫面設計的類別，用來將這些零散資料彙整成一個完美的「選單/展示」物件。
*   **範例**：`HeroVO.java` (整合了 `hero` 表、`hero_stats` 表以及頭像圖片的資訊)。

### 6. `service` (服務介面) 與 `service.impl` (服務實作)
*   **職責**：遊戲的**「核心大腦（業務邏輯層）」**。所有的遊戲規則（例如：經驗升級門檻、傷害計算公式 `ATK - DEF`、裝備掉落機率擲骰子）都在這裡運算。
    *   `service`：定義業務大綱，例如「啟動戰鬥」、「執行一秒戰鬥」。
    *   `service.impl`：寫出運算的具體程式碼步驟。
*   **💡 協調型服務 (Orchestration Service) 概念**：
    戰鬥系統（`BattleServiceImpl`）本身沒有專屬的 `battle` 資料表，因此不需要 `BattleDao`。它是透過「協調調度」`MonsterDao` 來撈怪物，以及 `HeroDao` 來儲存升級數據。這種不對接單一資料表、負責統籌多個 DAO 完成複雜規則的服務，就是協調型服務。
*   **範例**：`BattleService.java` (介面), `BattleServiceImpl.java` (實作)。

### 7. `util` (系統工具層)
*   **職責**：放一些**「無狀態的（Stateless）」**靜態小工具，或是開發過程中的快速測試程式。
*   **原則**：這裡的方法都是靜態方法（`static`），呼叫完就走，不在記憶體中保留任何狀態。
*   **範例**：`DbUtil.java` (關聯資料庫通道), `DbAlter.java` (重建資料庫表結構), `MonsterDaoTest.java` (測試用單元程式)。

---

## 💡 開發新功能「歸類指引」問與答

### ❓ 問題一：我要設計一個「背包 (Bag)」功能，需要開哪些檔案？放哪裡？
1.  **資料庫**：您需要一格背包表格 `hero_equipment`。
2.  **Entity (實體)** ➡️ 在 `entity` 資料夾新增 `HeroEquipment.java`（與資料表對應）。
3.  **DAO (資料庫讀寫)** ➡️ 在 `dao` 新增 `HeroEquipmentDao.java` 介面，在 `dao.impl` 新增 `HeroEquipmentDaoImpl.java` 實作。寫 SQL 查詢該角色背包裡的所有裝備。
4.  **Service (背包邏輯)** ➡️ 在 `service` 和 `service.impl` 新增 `BagService` / `BagServiceImpl`。寫穿戴裝備的規則（例如：穿上武器後角色攻擊力 +5，並在資料庫將 `is_equipped` 改成 1）。
5.  **UI (介面顯示)** ➡️ 在 `controller` 新增 `BagUI.java` (彈出 JDialog 視窗)，呼叫 `BagService` 獲取背包資料並顯示出來。

### ❓ 問題二：我要在背包裡顯示「裝備的名稱與它的加成數值」，但我撈出來的資料被拆在 `hero_equipment` 和 `equipment` 兩張表，該怎麼傳給 UI？
*   **答案**：寫一個 SQL Join 語句將兩張表結合，然後在 **`vo`** 資料夾建立一個 `EquipmentVO.java`（包含：裝備在背包的唯一流水號、名字、部位、攻擊力加成、是否已穿戴）。UI 只需要讀取 List<`EquipmentVO`> 即可輕鬆繪製背包格子！

---

🏆 **牢記這套分工，未來不論是個人專題、期末作業還是業界大型專案，您的程式碼結構都將是殿堂級的整潔與專業！**
