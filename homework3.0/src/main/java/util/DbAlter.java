package util;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

/**
 * 資料庫結構與資料初始化工具 (DbAlter)
 * 💡 專門用來在資料庫中建立怪物資料表 `monster` 並寫入 9 隻預設怪物的資料。
 * 
 * 💡【如何在 Eclipse 中執行它？】
 *   1. 在專案中選中此檔案。
 *   2. 按右鍵選擇「Run As」 ➡️ 「Java Application」。
 *   3. 下方 Console 主控台顯示「資料庫初始化成功」即代表完成！
 */
public class DbAlter {

    public static void main(String[] args) {
        System.out.println("=======================================================");
        System.out.println("        資料庫初始化 - 建立 monster 表與插入模板資料       ");
        System.out.println("=======================================================");

        // 1. 建立怪物資料表的 SQL 語句
        String sqlCreateMonsterTable = 
            "CREATE TABLE IF NOT EXISTS `monster` (" +
            "  `id` int NOT NULL AUTO_INCREMENT COMMENT '怪物的唯一 ID'," +
            "  `name` varchar(50) NOT NULL COMMENT '怪物名稱'," +
            "  `map_id` int NOT NULL COMMENT '所屬地圖 ID (1:新手草原, 2:幽暗森林, 3:烈焰火山)'," +
            "  `level` int DEFAULT '1' COMMENT '怪物推薦等級'," +
            "  `hp` int NOT NULL DEFAULT '30' COMMENT '怪物生命值上限'," +
            "  `atk` int NOT NULL DEFAULT '5' COMMENT '怪物攻擊力'," +
            "  `def` int NOT NULL DEFAULT '2' COMMENT '怪物防禦力'," +
            "  `speed` int NOT NULL DEFAULT '5' COMMENT '怪物速度'," +
            "  `exp_reward` int NOT NULL DEFAULT '10' COMMENT '擊敗後的經驗值'," +
            "  `drop_rate` double DEFAULT '0.05' COMMENT '裝備掉落率 (如 0.05 代表 5% 掉落率)'," +
            "  `loot_tier` int DEFAULT '1' COMMENT '掉落裝備階級 (1: 基礎, 2: 中級, 3: 高級)'," +
            "  PRIMARY KEY (`id`)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";

        // 2. 建立裝備範本表的 SQL
        String sqlCreateEquipmentTable = 
            "CREATE TABLE IF NOT EXISTS `equipment` (" +
            "  `id` int NOT NULL AUTO_INCREMENT," +
            "  `name` varchar(50) NOT NULL COMMENT '裝備名稱'," +
            "  `slot` varchar(20) NOT NULL COMMENT '裝備位置 (helmet, armor, weapon, boots)'," +
            "  `tier` int DEFAULT '1' COMMENT '裝備品級 (1, 2, 3)'," +
            "  `hp_bonus` int DEFAULT '0' COMMENT '生命值加成'," +
            "  `atk_bonus` int DEFAULT '0' COMMENT '攻擊力加成'," +
            "  `def_bonus` int DEFAULT '0' COMMENT '防禦力加成'," +
            "  `speed_bonus` int DEFAULT '0' COMMENT '速度加成'," +
            "  PRIMARY KEY (`id`)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";

        // 3. 建立角色擁有裝備表的 SQL (含外鍵約束)
        String sqlCreateHeroEquipmentTable = 
            "CREATE TABLE IF NOT EXISTS `hero_equipment` (" +
            "  `id` int NOT NULL AUTO_INCREMENT," +
            "  `hero_id` int NOT NULL COMMENT '擁有者角色 ID'," +
            "  `equipment_id` int NOT NULL COMMENT '裝備範本 ID'," +
            "  `is_equipped` tinyint DEFAULT '0' COMMENT '是否穿戴中 (1:穿戴, 0:背包中)'," +
            "  PRIMARY KEY (`id`)," +
            "  CONSTRAINT `fk_hero_equip_hero` FOREIGN KEY (`hero_id`) REFERENCES `hero` (`id`) ON DELETE CASCADE," +
            "  CONSTRAINT `fk_hero_equip_item` FOREIGN KEY (`equipment_id`) REFERENCES `equipment` (`id`) ON DELETE CASCADE" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";

        // 4. 插入數據 SQL
        String sqlTruncateMonster = "TRUNCATE TABLE `monster`;";
        String sqlInsertMonsters = 
            "INSERT INTO `monster` (name, map_id, level, hp, atk, def, speed, exp_reward, drop_rate, loot_tier) VALUES " +
            "('綠史萊姆', 1, 1, 30, 4, 1, 4, 8, 0.05, 1)," +
            "('幼草蛇', 1, 2, 40, 6, 2, 6, 12, 0.06, 1)," +
            "('小野狼', 1, 4, 60, 9, 3, 8, 20, 0.08, 1)," +
            "('哥布林', 2, 6, 100, 14, 5, 10, 35, 0.10, 2)," +
            "('毒蜘蛛', 2, 8, 130, 18, 7, 12, 50, 0.12, 2)," +
            "('食人花', 2, 10, 180, 24, 10, 8, 70, 0.15, 2)," +
            "('熔岩魔', 3, 11, 250, 32, 14, 10, 100, 0.18, 3)," +
            "('火蜥蜴', 3, 13, 320, 40, 18, 14, 140, 0.20, 3)," +
            "('烈焰巨龍 (BOSS)', 3, 15, 500, 55, 25, 12, 250, 0.35, 3);";

        String sqlTruncateEquipment = "TRUNCATE TABLE `equipment`;";
        String sqlInsertEquipment = 
            "INSERT INTO `equipment` (name, slot, tier, hp_bonus, atk_bonus, def_bonus, speed_bonus) VALUES " +
            "('新手木劍', 'weapon', 1, 0, 2, 0, 0)," +
            "('皮革帽', 'helmet', 1, 15, 0, 0, 0)," +
            "('新手布衣', 'armor', 1, 0, 0, 1, 0)," +
            "('草鞋', 'boots', 1, 0, 0, 0, 1)," +
            "('精鋼大劍', 'weapon', 2, 0, 5, 0, 0)," +
            "('騎士頭盔', 'helmet', 2, 35, 0, 0, 0)," +
            "('鎖子甲', 'armor', 2, 0, 0, 3, 0)," +
            "('皮靴', 'boots', 2, 0, 0, 0, 2)," +
            "('熔岩神劍', 'weapon', 3, 0, 12, 0, 0)," +
            "('熔岩頭盔', 'helmet', 3, 80, 0, 0, 0)," +
            "('熔岩胸甲', 'armor', 3, 0, 0, 8, 0)," +
            "('重裝戰靴', 'boots', 3, 0, 0, 0, 4);";

        // 5. 建立角色背包裝備檢視表 (View) 的 SQL 語句 (結合 hero_equipment 與 equipment 兩張表)
        String sqlCreateHeroEquipmentView = 
            "CREATE OR REPLACE VIEW `v_hero_equipment` AS " +
            "SELECT " +
            "  he.id AS id, " +
            "  he.hero_id AS hero_id, " +
            "  he.equipment_id AS equipment_id, " +
            "  he.is_equipped AS is_equipped, " +
            "  e.name AS name, " +
            "  e.slot AS slot, " +
            "  e.tier AS tier, " +
            "  e.hp_bonus AS hp_bonus, " +
            "  e.atk_bonus AS atk_bonus, " +
            "  e.def_bonus AS def_bonus, " +
            "  e.speed_bonus AS speed_bonus " +
            "FROM hero_equipment he " +
            "INNER JOIN equipment e ON he.equipment_id = e.id;";

        Connection conn = null;
        Statement stmt = null;

        try {
            conn = DbUtil.getConnection();
            stmt = conn.createStatement();

            // 💡 執行零：先刪除舊表 (特別注意：因為有外鍵約束，必須先刪除子表 hero_equipment，才能刪除父表 equipment)
            System.out.println("[步驟 0-1] 正在關閉外鍵約束檢查並刪除舊表結構...");
            stmt.execute("DROP VIEW IF EXISTS `v_hero_equipment`;"); // 先刪除檢視表
            stmt.execute("DROP TABLE IF EXISTS `hero_equipment`;");
            stmt.execute("DROP TABLE IF EXISTS `equipment`;");
            stmt.execute("DROP TABLE IF EXISTS `monster`;");

            // 💡 執行一：建立資料表與檢視表
            System.out.println("[步驟 1-1] 正在建立 monster 資料表...");
            stmt.execute(sqlCreateMonsterTable);
            
            System.out.println("[步驟 1-2] 正在建立 equipment 資料表...");
            stmt.execute(sqlCreateEquipmentTable);
            
            System.out.println("[步驟 1-3] 正在建立 hero_equipment 資料表...");
            stmt.execute(sqlCreateHeroEquipmentTable);

            System.out.println("[步驟 1-4] 正在建立 v_hero_equipment 檢視表...");
            stmt.execute(sqlCreateHeroEquipmentView);

            System.out.println("[步驟 1-5] 正在升級 hero_stats 資料表與 v_hero_detail 檢視表...");
            try {
                stmt.execute("ALTER TABLE hero_stats ADD COLUMN stat_points INT DEFAULT 0 COMMENT '未分配屬性點';");
                System.out.println("  -> [成功] 已在 hero_stats 資料表中新增 stat_points 欄位。");
            } catch (SQLException e) {
                // MySQL 錯誤代碼 1060 代表欄位已存在，此為正常防呆
                if (e.getErrorCode() == 1060) {
                    System.out.println("  -> [提示] stat_points 欄位已存在，不需要重複新增。");
                } else {
                    throw e; // 其他 SQL 異常則拋出
                }
            }

            // 💡 移除舊有 crit_rate 屬性欄位 (若已移除則跳過)
            try {
                stmt.execute("ALTER TABLE hero_stats DROP COLUMN crit_rate;");
                System.out.println("  -> [成功] 已在 hero_stats 資料表中移除 crit_rate 欄位。");
            } catch (SQLException e) {
                System.out.println("  -> [提示] crit_rate 欄位已移除，或不存在。");
            }

            // 💡 重新建立 v_hero_detail 檢視表，使其包含 s.stat_points 欄位 (移除 crit_rate)
            String sqlCreateHeroDetailView = 
                "CREATE OR REPLACE VIEW `v_hero_detail` AS " +
                "SELECT " +
                "  h.id AS id, " +
                "  h.player_id AS player_id, " +
                "  h.name AS name, " +
                "  h.gender AS gender, " +
                "  h.img_name AS img_name, " +
                "  s.level AS level, " +
                "  s.exp AS exp, " +
                "  s.hp AS hp, " +
                "  s.atk AS atk, " +
                "  s.def AS def, " +
                "  s.speed AS speed, " +
                "  s.stat_points AS stat_points " +
                "FROM hero h " +
                "INNER JOIN hero_stats s ON h.id = s.hero_id;";
            stmt.execute(sqlCreateHeroDetailView);
            System.out.println("  -> [成功] 已重新建立 v_hero_detail 檢視表 (含 stat_points，已移除 crit_rate)。");

            System.out.println("-> [成功] 所有資料表與檢視表已建立與升級就緒！");

            // 💡 執行二：重置並寫入怪物資料
            System.out.println("[步驟 2-1] 正在寫入預設怪物模板數據...");
            stmt.execute(sqlTruncateMonster);
            stmt.execute(sqlInsertMonsters);
            
            // 💡 執行三：重置並寫入裝備資料
            System.out.println("[步驟 2-2] 正在寫入預設裝備模板數據...");
            // 關閉暫時的外鍵檢查以執行 TRUNCATE，避免外鍵衝突報錯
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");
            stmt.execute(sqlTruncateEquipment);
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
            stmt.execute(sqlInsertEquipment);
            
            System.out.println("-> [成功] 預設怪物與裝備數據寫入完畢！");

            System.out.println("\n🎉 資料庫更新成功！怪物與裝備系統已成功建立！");

        } catch (Exception e) {
            System.err.println("\n❌ 資料庫更新失敗，錯誤原因: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.close(null, stmt, conn);
        }
        System.out.println("=======================================================");
    }
}
