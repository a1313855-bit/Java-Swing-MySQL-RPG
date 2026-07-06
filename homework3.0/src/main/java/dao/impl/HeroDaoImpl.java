package dao.impl;

import dao.HeroDao;
import entity.Hero;
import entity.HeroStats;
import vo.HeroVO;
import util.DbUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 角色資料存取實作類別 (HeroDaoImpl)
 * 使用 JDBC 直接與 MySQL 資料庫進行互動。
 * 
 * 💡【核心語法學習：try-catch-finally 結構說明】
 * 
 * 1. 什麼是 finally？
 *    - 不論 try 區塊裡的程式是「順利跑完」還是「中途出錯跳進 catch」，
 *      finally 區塊中的程式碼都【保證一定會執行】！
 * 
 * 2. 為什麼釋放資源（關閉連線）一定要寫在 finally 區塊？
 *    - 在 Java 中，開啟資料庫連線（Connection）就像是向資料庫借用一條專屬的通道。
 *      資料庫的通道數量是有限的，如果用完不關閉，就會被一直佔著，這叫「連線洩漏 (Connection Leak)」。
 *      一旦連線被佔滿，遊戲就會當機，因為後續所有功能都無法連上資料庫。
 *    - 如果把關閉連線寫在 try 的最後面，一旦執行 SQL 出錯，程式會直接跳去 catch，
 *      導致「關閉連線」那行程式被跳過、無法執行！
 *    - 因此，寫在 finally 中，不論成功或失敗，最後都能安全關閉連線，保證伺服器不卡死。
 * 
 * 3. 📖 圖書館借書的比喻（加深記憶）：
 *    - try ➡️ 您把書借回家讀（執行程式）。
 *    - catch ➡️ 讀書時不小心把咖啡灑在書上，或是書頁破損了（發生異常錯誤）。
 *    - finally ➡️ 【不論您有沒有順利把書讀完，或是咖啡灑在書上，最後您「一定都得把書還給圖書館」！】
 *      （這就是關閉資源，如果不還，下一個玩家就借不到書了）。
 */
public class HeroDaoImpl implements HeroDao {

    /**
     * 1. 讀取該玩家所有角色 (查詢虛擬檢視表 v_hero_detail)
     * 💡【學習筆記】
     * 雖然在資料庫中我們將資料分開在 hero 和 hero_stats 兩張表，但因為我們建立了 View，
     * 所以在 Java 中，我們不需要寫複雜的 JOIN 查詢，直接 SELECT View 即可，非常簡單乾淨！
     */
    @Override
    public List<HeroVO> selectByPlayerId(int playerId) {
        List<HeroVO> list = new ArrayList<>();
        String sql = "SELECT * FROM v_hero_detail WHERE player_id = ?";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DbUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, playerId);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                HeroVO vo = new HeroVO();
                vo.setId(rs.getInt("id"));
                vo.setPlayerId(rs.getInt("player_id"));
                vo.setName(rs.getString("name"));
                vo.setGender(rs.getString("gender"));
                vo.setImgName(rs.getString("img_name"));
                vo.setLevel(rs.getInt("level"));
                vo.setExp(rs.getInt("exp"));
                vo.setHp(rs.getInt("hp")); // 💡 新增：載入生命值
                vo.setAtk(rs.getInt("atk"));
                vo.setDef(rs.getInt("def"));
                vo.setSpeed(rs.getInt("speed"));
                vo.setStatPoints(rs.getInt("stat_points")); // 💡 新增：載入屬性點
                list.add(vo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtil.close(rs, ps, conn); // 💡 修正：參數順序必須為 (ResultSet, Statement, Connection)
        }
        return list;
    }

    /**
     * 2. 創建新角色 (💡 核心技術：JDBC 交易/Transaction 事務機制)
     * 💡【學習筆記】
     * 為了防止「基本表 hero 新增成功，但數值表 hero_stats 新增失敗」的半吊子狀況，
     * 我們必須關閉自動提交，在同一個 Connection 中執行兩次寫入，成功才 commit，失敗則 rollback！
     */
    @Override
    public boolean insert(Hero hero, HeroStats stats) {
        String sqlHero = "INSERT INTO hero (player_id, name, gender, img_name) VALUES (?, ?, ?, ?)";
        String sqlStats = "INSERT INTO hero_stats (hero_id, level, exp, hp, atk, def, speed) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement psHero = null;
        PreparedStatement psStats = null;
        ResultSet rsKeys = null;
        
        try {
            conn = DbUtil.getConnection();
            
            // 💡【關鍵步驟一：開啟交易】
            // 預設是 true (每執行完一條 SQL 就直接寫入資料庫)。我們改為 false，代表「手動提交」。
            conn.setAutoCommit(false);
            
            // 💡【關鍵步驟二：寫入第一張表 hero】
            // 因為我們要取得資料庫自動生成的角色 ID (Auto_Increment)，所以要加上 Statement.RETURN_GENERATED_KEYS
            psHero = conn.prepareStatement(sqlHero, Statement.RETURN_GENERATED_KEYS);
            psHero.setInt(1, hero.getPlayerId());
            psHero.setString(2, hero.getName());
            psHero.setString(3, hero.getGender());
            psHero.setString(4, hero.getImgName());
            
            int affectedRows = psHero.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("創建角色基本資料失敗，沒有影響任何行數。");
            }
            
            // 取得剛才寫入 hero 表後，資料庫自動生成的角色 id
            rsKeys = psHero.getGeneratedKeys();
            int newHeroId = -1;
            if (rsKeys.next()) {
                newHeroId = rsKeys.getInt(1); // 拿到了新角色的唯一 ID
            } else {
                throw new SQLException("創建角色失敗，無法取得產生的角色 ID。");
            }
            
            // 💡【關鍵步驟三：寫入第二張表 hero_stats】
            // 我們拿著剛才取出的 newHeroId，作為這筆戰鬥數值的關聯 ID (外鍵)
            psStats = conn.prepareStatement(sqlStats);
            psStats.setInt(1, newHeroId);
            psStats.setInt(2, stats.getLevel());
            psStats.setInt(3, stats.getExp());
            psStats.setInt(4, stats.getHp()); // 💡 新增：綁定生命值
            psStats.setInt(5, stats.getAtk());
            psStats.setInt(6, stats.getDef());
            psStats.setInt(7, stats.getSpeed());
            
            psStats.executeUpdate();
            
            // 💡【關鍵步驟四：確認兩張表都成功後，正式提交交易】
            // 這時候資料庫才會真正將資料寫入硬碟中
            conn.commit();
            System.out.println("角色交易寫入成功！角色ID: " + newHeroId + ", 角色名稱: " + hero.getName());
            return true;
            
        } catch (SQLException e) {
            // 💡【關鍵步驟五：出錯時進行回滾 (Rollback)】
            // 萬一中間有任何一行拋出異常，我們會執行 rollback，清除剛才嘗試寫入的一切資料！
            System.err.println("交易執行失敗！進行回滾操作。原因: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            // 💡【關鍵步驟六：還原自動提交狀態並關閉資源】
            // 還原成預設的自動提交 (AutoCommit = true)，避免影響到其他查詢的連線
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            // 關閉各個 PreparedStatement
            try {
                if (psHero != null) psHero.close();
                if (psStats != null) psStats.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DbUtil.close(rsKeys, null, conn); // 💡 修正：參數順序必須為 (ResultSet, Statement, Connection)
        }
    }

    /**
     * 3. 刪除角色
     * 💡【學習筆記】
     * 我們真的只需要對 hero 資料表進行 DELETE！
     * 因為 MySQL 的 hero_stats 外鍵有設定 ON DELETE CASCADE 關聯，
     * 資料庫內部會自己幫我們把對應的 stats 資料清除，所以 Java 端的程式寫起來非常輕鬆，只要刪一個表即可！
     */
    @Override
    public boolean deleteById(int heroId) {
        String sql = "DELETE FROM hero WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DbUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, heroId);
            int rows = ps.executeUpdate();
            return rows > 0; // 若刪除成功，影響的列數會大於 0
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DbUtil.close(null, ps, conn); // 💡 修正：參數順序必須為 (ResultSet, Statement, Connection)
        }
    }

    /**
     * 4. 修改角色名稱
     */
    @Override
    public boolean updateName(int heroId, String newName) {
        String sql = "UPDATE hero SET name = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DbUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, newName);
            ps.setInt(2, heroId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DbUtil.close(null, ps, conn); // 💡 修正：參數順序必須為 (ResultSet, Statement, Connection)
        }
    }

    /**
     * 5. 修改角色造型圖片
     */
    @Override
    public boolean updateImgName(int heroId, String newImgName) {
        String sql = "UPDATE hero SET img_name = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DbUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, newImgName);
            ps.setInt(2, heroId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DbUtil.close(null, ps, conn); // 💡 修正：參數順序必須為 (ResultSet, Statement, Connection)
        }
    }

    /**
     * 6. 根據玩家 ID 與角色名稱查詢特定角色明細 (用於重複名稱檢查)
     */
    @Override
    public List<HeroVO> selectByPlayerIdAndHeroName(int playerId, String heroName) {
        List<HeroVO> list = new ArrayList<>();
        String sql = "SELECT * FROM v_hero_detail WHERE player_id = ? AND name = ?";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DbUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, playerId);
            ps.setString(2, heroName);
            rs = ps.executeQuery();
            
            // 將查詢出的結果（若有符合）包裝成 HeroVO 放進 List 中
            while (rs.next()) {
                HeroVO vo = new HeroVO();
                vo.setId(rs.getInt("id"));
                vo.setPlayerId(rs.getInt("player_id"));
                vo.setName(rs.getString("name"));
                vo.setGender(rs.getString("gender"));
                vo.setImgName(rs.getString("img_name"));
                vo.setLevel(rs.getInt("level"));
                vo.setExp(rs.getInt("exp"));
                vo.setHp(rs.getInt("hp"));
                vo.setAtk(rs.getInt("atk"));
                vo.setDef(rs.getInt("def"));
                vo.setSpeed(rs.getInt("speed"));
                vo.setStatPoints(rs.getInt("stat_points")); // 💡 新增：載入屬性點
                list.add(vo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 安全釋放資料庫連接通道資源
            DbUtil.close(rs, ps, conn);
        }
        return list;
    }

    /**
     * 7. 更新指定角色的等級與經驗值 (儲存升級數據)
     */
    @Override
    public boolean updateLevelAndExp(int heroId, int level, int exp) {
        String sql = "UPDATE hero_stats SET level = ?, exp = ? WHERE hero_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DbUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, level);
            ps.setInt(2, exp);
            ps.setInt(3, heroId);
            int rows = ps.executeUpdate();
            return rows > 0; // 若受影響行數大於 0，代表更新成功
        } catch (SQLException e) {
            System.err.println("❌ 更新角色等級與經驗值時發生 SQL 異常，原因: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DbUtil.close(null, ps, conn);
        }
    }

    /**
     * 8. 根據角色 ID 查詢單個角色屬性 (用於屬性重算)
     */
    @Override
    public HeroVO selectByHeroId(int heroId) {
        String sql = "SELECT * FROM v_hero_detail WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DbUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, heroId);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                HeroVO vo = new HeroVO();
                vo.setId(rs.getInt("id"));
                vo.setPlayerId(rs.getInt("player_id"));
                vo.setName(rs.getString("name"));
                vo.setGender(rs.getString("gender"));
                vo.setImgName(rs.getString("img_name"));
                vo.setLevel(rs.getInt("level"));
                vo.setExp(rs.getInt("exp"));
                vo.setHp(rs.getInt("hp"));
                vo.setAtk(rs.getInt("atk"));
                vo.setDef(rs.getInt("def"));
                vo.setSpeed(rs.getInt("speed"));
                vo.setStatPoints(rs.getInt("stat_points")); // 💡 新增：載入屬性點
                return vo;
            }
        } catch (SQLException e) {
            System.err.println("❌ 根據 ID 查詢角色詳細資料時發生 SQL 異常，原因: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.close(rs, ps, conn);
        }
        return null;
    }

    @Override
    public boolean updateStats(HeroVO hero) {
        String sql = "UPDATE hero_stats SET level = ?, exp = ?, hp = ?, atk = ?, def = ?, speed = ?, stat_points = ? WHERE hero_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DbUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, hero.getLevel());
            ps.setInt(2, hero.getExp());
            ps.setInt(3, hero.getHp());
            ps.setInt(4, hero.getAtk());
            ps.setInt(5, hero.getDef());
            ps.setInt(6, hero.getSpeed());
            ps.setInt(7, hero.getStatPoints());
            ps.setInt(8, hero.getId());

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("❌ 更新角色全屬性（含配點）時發生 SQL 異常，原因: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DbUtil.close(null, ps, conn);
        }
    }
}
