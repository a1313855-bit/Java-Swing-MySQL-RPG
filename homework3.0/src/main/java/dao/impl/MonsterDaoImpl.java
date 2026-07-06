package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.MonsterDao;
import entity.Monster;
import util.DbUtil;

/**
 * 怪物資料存取實作類別 (MonsterDaoImpl)
 * 💡 實作對 monster 表的 JDBC 查詢操作。
 */
public class MonsterDaoImpl implements MonsterDao {

    @Override
    public List<Monster> selectByMapId(int mapId) {
        List<Monster> list = new ArrayList<>();
        String sql = "SELECT * FROM monster WHERE map_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // 1. 取得資料庫連接
            conn = DbUtil.getConnection();
            
            // 2. 建立預編譯陳述式
            ps = conn.prepareStatement(sql);
            ps.setInt(1, mapId); // 綁定地圖 ID 參數
            
            // 3. 執行查詢取得結果集
            rs = ps.executeQuery();

            // 4. 循環結果集，將每一筆怪物資料包裝為 Monster 實體並放進清單中
            while (rs.next()) {
                Monster monster = new Monster();
                monster.setId(rs.getInt("id"));
                monster.setMapId(rs.getInt("map_id"));
                monster.setName(rs.getString("name"));
                monster.setLevel(rs.getInt("level"));
                monster.setHp(rs.getInt("hp"));
                monster.setAtk(rs.getInt("atk"));
                monster.setDef(rs.getInt("def"));
                monster.setSpeed(rs.getInt("speed"));
                monster.setExpReward(rs.getInt("exp_reward"));
                monster.setDropRate(rs.getDouble("drop_rate"));
                monster.setLootTier(rs.getInt("loot_tier"));
                list.add(monster);
            }
        } catch (SQLException e) {
            System.err.println("❌ 查詢地圖怪物時發生 SQL 異常，原因: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 5. 💡 防呆釋放資源：依序關閉結果集、陳述式與連線
            DbUtil.close(rs, ps, conn);
        }
        
        return list;
    }
}
