package dao.impl;

import dao.PlayerDao;
import entity.Player;
import util.DbUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 玩家資料存取實作類別 (PlayerDaoImpl)
 * 使用 JDBC 具體實作對 MySQL 資料庫的讀寫操作。
 */
public class PlayerDaoImpl implements PlayerDao {

    /**
     * 新增玩家帳號
     * 💡 觀念：使用 PreparedStatement 可以有效防範 SQL 注入攻擊（SQL Injection）。
     */
    @Override
    public boolean insertPlayer(Player player) {
        String sql = "INSERT INTO player (username, password, nick_name, mail) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // 1. 取得連線
            conn = DbUtil.getConnection();
            // 2. 準備 SQL 語句
            pstmt = conn.prepareStatement(sql);
            // 3. 設定參數 (1-indexed)
            pstmt.setString(1, player.getUsername());
            pstmt.setString(2, player.getPassword());
            pstmt.setString(3, player.getNickName());
            pstmt.setString(4, player.getMail());

            // 4. 執行更新，返回受影響的行數
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 5. 不管成功或失敗，一定要釋放資源
            DbUtil.close(null, pstmt, conn);
        }
        return false;
    }

    /**
     * 根據帳號名稱查詢玩家
     */
    @Override
    public Player selectByUsernameAndPassword(String username,String password) {
        String sql = "SELECT * FROM player WHERE username = ? and password = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // 1. 取得連線
            conn = DbUtil.getConnection();
            // 2. 準備 SQL 語句
            pstmt = conn.prepareStatement(sql);
            // 3. 設定參數
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            // 4. 執行查詢，取得結果集
            rs = pstmt.executeQuery();

            // 5. 若有下一筆資料，將資料庫欄位內容封裝進 Player 物件
            if (rs.next()) {
                Player player = new Player();
                player.setId(rs.getInt("id"));
                player.setUsername(rs.getString("username"));
                player.setPassword(rs.getString("password"));
                // 💡 注意：將資料庫的蛇形欄位 nick_name 轉為駝峰屬性 nickName
                player.setNickName(rs.getString("nick_name"));
                player.setMail(rs.getString("mail"));
                player.setCreateTime(rs.getTimestamp("create_time"));
                return player;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 5. 關閉所有資料庫資源
            DbUtil.close(rs, pstmt, conn);
        }
        return null;
    }

    @Override
    public Player selectByUsername(String username) {
        String sql = "SELECT * FROM player WHERE username = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Player player = new Player();
                player.setId(rs.getInt("id"));
                player.setUsername(rs.getString("username"));
                player.setPassword(rs.getString("password"));
                player.setNickName(rs.getString("nick_name"));
                player.setMail(rs.getString("mail"));
                player.setCreateTime(rs.getTimestamp("create_time"));
                return player;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtil.close(rs, pstmt, conn);
        }
        return null;
    }

    @Override
    public Player selectBynickName(String nickName) {
        String sql = "SELECT * FROM player WHERE nick_name = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nickName);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Player player = new Player();
                player.setId(rs.getInt("id"));
                player.setUsername(rs.getString("username"));
                player.setPassword(rs.getString("password"));
                player.setNickName(rs.getString("nick_name"));
                player.setMail(rs.getString("mail"));
                player.setCreateTime(rs.getTimestamp("create_time"));
                return player;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtil.close(rs, pstmt, conn);
        }
        return null;
    }

    @Override
    public Player selectByMail(String mail) {
        String sql = "SELECT * FROM player WHERE mail = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, mail);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Player player = new Player();
                player.setId(rs.getInt("id"));
                player.setUsername(rs.getString("username"));
                player.setPassword(rs.getString("password"));
                player.setNickName(rs.getString("nick_name"));
                player.setMail(rs.getString("mail"));
                player.setCreateTime(rs.getTimestamp("create_time"));
                return player;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtil.close(rs, pstmt, conn);
        }
        return null;
    }

    @Override
    public boolean updatePassword(int id, String newPassword) {
        String sql = "UPDATE player SET password = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DbUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, id);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtil.close(null, pstmt, conn);
        }
        return false;
    }

    // ==========================================
    // 💡 暫時的測試 main 程式
    // ==========================================
    public static void main(String[] args) {
        PlayerDao dao = new PlayerDaoImpl();

        System.out.println("======= 開始測試 PlayerDaoImpl =======");

        // 準備測試資料
        long timestamp = System.currentTimeMillis() / 1000;
        String testUsername = "hero_" + timestamp;
        String testMail = "hero_" + timestamp + "@game.com";

        // 測試 1：新增/註冊新帳號
        System.out.println("\n--- 測試 1：註冊新帳號 ---");
        Player newPlayer = new Player(testUsername, "pwd123", "冒險家小智", testMail);
        boolean isSuccess = dao.insertPlayer(newPlayer);
        System.out.println("註冊結果：" + (isSuccess ? "【成功】" : "【失敗】"));

        // 測試 2：根據帳密查詢玩家 (模擬登入)
        System.out.println("\n--- 測試 2：使用正確的帳密登入 ---");
        Player queriedPlayer = dao.selectByUsernameAndPassword(testUsername, "pwd123");
        if (queriedPlayer != null) {
            System.out.println("登入成功！ID: " + queriedPlayer.getId() + ", 暱稱: " + queriedPlayer.getNickName());
        } else {
            System.out.println("【登入失敗】找不到此帳號或密碼錯誤！");
        }

        // 測試 3：根據信箱查詢玩家 (模擬忘記密碼身分驗證)
        System.out.println("\n--- 測試 3：使用信箱查詢玩家 ---");
        Player playerByMail = dao.selectByMail(testMail);
        if (playerByMail != null) {
            System.out.println("信箱驗證成功！該信箱對應帳號為：" + playerByMail.getUsername());

            // 測試 4：根據玩家 ID 修改密碼
            System.out.println("\n--- 測試 4：重設玩家密碼 ---");
            boolean updateSuccess = dao.updatePassword(playerByMail.getId(), "newSecurePwd789");
            System.out.println("修改密碼結果：" + (updateSuccess ? "【成功】" : "【失敗】"));

            // 驗證用舊密碼登入應失敗，新密碼登入應成功
            System.out.println("\n--- 驗證新密碼是否生效 ---");
            Player loginOld = dao.selectByUsernameAndPassword(testUsername, "pwd123");
            Player loginNew = dao.selectByUsernameAndPassword(testUsername, "newSecurePwd789");
            System.out.println("使用舊密碼登入：" + (loginOld != null ? "【成功】" : "【失敗(預期結果)】"));
            System.out.println("使用新密碼登入：" + (loginNew != null ? "【成功(預期結果)】" : "【失敗】"));
        } else {
            System.out.println("【驗證失敗】找不到該信箱對應的帳號！");
        }

        System.out.println("\n======================================");
    }
}
