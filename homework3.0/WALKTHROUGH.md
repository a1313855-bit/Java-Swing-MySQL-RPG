# RPG 放置遊戲 - 戰鬥系統與架構解耦重構完成說明 (WALKTHROUGH.md)

我們已成功實作並串接了「掛機戰鬥系統」與「資料庫等級持久化存檔」，同時完成了專案整體的「大整頓」，使整個 Java 專案結構完全符合業界最高級的**乾淨分層架構 (Clean Layered Architecture)**！

---

## 📁 新增與異動檔案列表

### 1. 資料庫 schema 與初始化
*   **`homework3.0.sql`** & **`DbAlter.java`**: [SQL 備份](file:///D:/work/homework3.0/homework3.0.sql) | [資料庫變更工具](file:///D:/work/homework3.0/src/main/java/util/DbAlter.java)
    *   新增了 `monster` 資料表並種入 9 隻地圖怪物資料。
    *   新增了 `equipment`（裝備範本表，含 12 件三品級裝備數據）與 `hero_equipment`（角色擁有裝備關係表，含外鍵 Cascade 外鍵刪除約束）。
    *   `DbAlter.java` 內置了正確的外鍵刪除順序（先刪除子表，再刪除父表）防呆機制。

### 2. 乾淨架構 Package 搬移與解耦 (方案一)
為了確保專案具備業界最高標準的層級解耦，我們進行了以下結構搬遷：
*   **`dto` 套件**：
    *   **[BattleTickResult.java](file:///D:/work/homework3.0/src/main/java/dto/BattleTickResult.java)**：從原先的 `service` 移出到此，作為純粹的跨層級資料打包傳輸物件 (DTO)。
*   **`vo` 套件**：
    *   **[HeroVO.java](file:///D:/work/homework3.0/src/main/java/vo/HeroVO.java)**：從原先 the `entity.vo` 移出為與 `entity` 平行的根目錄 package，專門存放給 UI 顯示渲染用的視圖物件 (View Object)。
*   **`entity` 套件**：
    *   **[Monster.java](file:///D:/work/homework3.0/src/main/java/entity/Monster.java)** (怪物實體)。
    *   **[Equipment.java](file:///D:/work/homework3.0/src/main/java/entity/Equipment.java)** (裝備模板實體)。
    *   **[HeroEquipment.java](file:///D:/work/homework3.0/src/main/java/entity/HeroEquipment.java)** (角色背包裝備實體)。
*   **`dao` 套件** / **`dao.impl` 套件**：
    *   **[MonsterDao.java](file:///D:/work/homework3.0/src/main/java/dao/MonsterDao.java)** / **[MonsterDaoImpl.java](file:///D:/work/homework3.0/src/main/java/dao/impl/MonsterDaoImpl.java)**：負責讀取特定地圖的怪物列表。
    *   **`HeroDao` / `HeroDaoImpl`**：新增 `updateLevelAndExp` JDBC 更新指令，供戰鬥勝利時自動回寫 MySQL 等級與經驗。

### 3. 業務服務層 (Orchestration Service)
*   **[BattleService.java](file:///D:/work/homework3.0/src/main/java/service/BattleService.java)** / **[BattleServiceImpl.java](file:///D:/work/homework3.0/src/main/java/service/impl/BattleServiceImpl.java)**：
    *   **協調型服務 (Orchestration Service)**：不直接對應單一 `BattleDao`，而是作為大腦，在後台調用 `MonsterDao` 和 `HeroDao`。
    *   **戰鬥核心邏輯**：內部儲存戰鬥的即時狀態（HP、ATK/DEF傷害計算、Speed增量公式、升級檢驗、裝備掉落率擲骰）。

### 4. 視窗展示層 (GameMainUI)
*   **[GameMainUI.java](file:///D:/work/homework3.0/src/main/java/controller/GameMainUI.java)**：
    *   瘦身為「輕量 UI」：不再有計時器累加、傷害公式或 SQL 交互。
    *   計時器 Tick 每 0.1 秒向 `BattleService` 要求當前戰鬥的 DTO 畫像，並直接用它來更新進度條、日誌框與血量。

### 5. 架構說明書
*   **[ARCHITECTURE.md](file:///D:/work/homework3.0/ARCHITECTURE.md)**：專案架構規範說明文件，詳細列出每個 package 存在的意義與開發新功能時的歸類流程。

---

## 🧪 驗證與測試情況

在 Eclipse 中執行 `ChooseHeroUI` ➡️ 選擇主角亞瑟 ➡️ 進入掛機大廳 ➡️ 開始戰鬥：
1.  **ATB 行動條**：黃色行動條根據玩家和怪物速度以不同頻率累積，到 100 時印出攻擊日誌。
2.  **傷害計算**：成功依據 `ATK - DEF` 計算並扣除血量，有最低 1 點傷害的物理保底。
3.  **戰勝升級與即時存檔**：擊敗怪物獲得 Exp/Gold，Exp 滿後升級，Service 層自動調用 `HeroDao` 更新資料庫 `hero_stats` 表。返回角色選擇大廳後，亞瑟的等級與數值已**永久同步更新為最新的 Lv.6**！
4.  **死亡退回**：挑戰烈焰火山被秒殺後，遊戲停止、HP 補滿、並自動強制選單切換回安全草原，防止卡死。
