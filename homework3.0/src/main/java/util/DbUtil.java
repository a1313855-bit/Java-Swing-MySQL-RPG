package util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * 資料庫連接工具類別 (DbUtil)
 * 負責載入 db.properties 設定檔、MySQL 驅動程式，並提供取得與關閉資料庫連線的方法。
 */
public class DbUtil {
    private static Properties props = new Properties();

    // 靜態程式區塊：當此類別被 JVM 載入時，會自動執行一次，適合用來初始化設定
    static {
        // 從 ClassPath (即 src/main/resources) 下載入 db.properties 檔案
        try (InputStream in = DbUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new RuntimeException("找不到 db.properties 設定檔，請確認它存在於 src/main/resources 中！");
            }
            // 讀取屬性檔案內容
            props.load(in);
            // 註冊/載入 MySQL JDBC 驅動程式類別
            Class.forName(props.getProperty("db.driver"));
        } catch (IOException e) {
            throw new RuntimeException("讀取 db.properties 發生錯誤", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("找不到 MySQL JDBC 驅動程式，請確認 Maven 依賴已正確下載！", e);
        }
    }

    /**
     * 建立並取得與資料庫的連線物件 (Connection)
     * @return Connection 連線物件
     * @throws SQLException 當連線失敗或參數錯誤時拋出
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            props.getProperty("db.url"),
            props.getProperty("db.username"),
            props.getProperty("db.password")
        );
    }

    /**
     * 安全關閉資料庫資源的方法，釋放資料庫連接池，避免連線被佔滿（Memory Leak & Connection Leak）
     * 
     * @param rs 結果集 (ResultSet)
     * @param stmt SQL 執行器 (Statement / PreparedStatement)
     * @param conn 連線物件 (Connection)
     */
    public static void close(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
