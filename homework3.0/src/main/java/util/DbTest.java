package util;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 資料庫連線測試類別
 * 用來快速確認 Java 專案能否成功連接至本機的 MySQL 資料庫。
 */
public class DbTest {
    public static void main(String[] args) {
        System.out.println("======= 開始測試 MySQL 資料庫連線 =======");
        
        // 使用 try-with-resources 語法，程式結束時會自動關閉連線
        try (Connection conn = DbUtil.getConnection()) {
            if (conn != null) {
                System.out.println("【連線成功！】已成功建立與 MySQL 資料庫 (homework3.0) 的連線。");
                System.out.println("連線資訊：" + conn.getMetaData().getURL());
                System.out.println("資料庫產品名稱：" + conn.getMetaData().getDatabaseProductName());
                System.out.println("資料庫版本：" + conn.getMetaData().getDatabaseProductVersion());
            } else {
                System.out.println("【連線失敗！】取得的連線物件為 null。");
            }
        } catch (SQLException e) {
            System.err.println("【連線失敗！】無法連接到資料庫，請檢查以下事項：");
            System.err.println("1. MySQL 服務是否已在您的電腦啟動？");
            System.err.println("2. 是否已成功執行/匯入 homework3.0.sql 建立資料庫？");
            System.err.println("3. src/main/resources/db.properties 中的帳密（root/1234）或 port（3306）是否正確？");
            System.err.println("\n詳細錯誤訊息如下：");
            e.printStackTrace();
        }
        
        System.out.println("=========================================");
    }
}
