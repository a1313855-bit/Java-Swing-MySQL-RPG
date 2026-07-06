package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.EquipmentDao;
import entity.Equipment;
import util.DbUtil;

/**
 * 裝備範本資料存取實作類別 (EquipmentDaoImpl)
 * 💡 負責與 MySQL 資料庫中的 `equipment` 表進行 JDBC 查詢。
 */
public class EquipmentDaoImpl implements EquipmentDao {

    @Override
    public List<Equipment> selectByTier(int tier) {
        List<Equipment> list = new ArrayList<>();
        String sql = "SELECT * FROM equipment WHERE tier = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, tier);
            rs = ps.executeQuery();

            while (rs.next()) {
                Equipment eq = new Equipment();
                eq.setId(rs.getInt("id"));
                eq.setName(rs.getString("name"));
                eq.setSlot(rs.getString("slot"));
                eq.setTier(rs.getInt("tier"));
                eq.setHpBonus(rs.getInt("hp_bonus"));
                eq.setAtkBonus(rs.getInt("atk_bonus"));
                eq.setDefBonus(rs.getInt("def_bonus"));
                eq.setSpeedBonus(rs.getInt("speed_bonus"));
                list.add(eq);
            }
        } catch (SQLException e) {
            System.err.println("❌ 查詢裝備範本時發生 SQL 異常，原因: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.close(rs, ps, conn);
        }

        return list;
    }
}
