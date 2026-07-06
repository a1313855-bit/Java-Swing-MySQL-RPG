package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.HeroEquipmentDao;
import entity.HeroEquipment;
import vo.HeroEquipmentVO;
import util.DbUtil;

/**
 * 角色背包裝備資料存取實作類別 (HeroEquipmentDaoImpl)
 * 💡 實作對 `hero_equipment` 表的寫入與更新，以及 `v_hero_equipment` 檢視表的讀取。
 */
public class HeroEquipmentDaoImpl implements HeroEquipmentDao {

    @Override
    public List<HeroEquipmentVO> selectByHeroId(int heroId) {
        List<HeroEquipmentVO> list = new ArrayList<>();
        // 💡 直接對我們昨天建立的檢視表進行簡單的查詢，避開了複雜的 JOIN 程式碼
        String sql = "SELECT * FROM v_hero_equipment WHERE hero_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, heroId);
            rs = ps.executeQuery();

            while (rs.next()) {
                HeroEquipmentVO vo = new HeroEquipmentVO();
                vo.setId(rs.getInt("id"));
                vo.setHeroId(rs.getInt("hero_id"));
                vo.setEquipmentId(rs.getInt("equipment_id"));
                // 💡 JDBC 驅動會自動將資料庫中的 tinyint (0/1) 轉成 Java 的 boolean
                vo.setEquipped(rs.getBoolean("is_equipped"));
                vo.setName(rs.getString("name"));
                vo.setSlot(rs.getString("slot"));
                vo.setTier(rs.getInt("tier"));
                vo.setHpBonus(rs.getInt("hp_bonus"));
                vo.setAtkBonus(rs.getInt("atk_bonus"));
                vo.setDefBonus(rs.getInt("def_bonus"));
                vo.setSpeedBonus(rs.getInt("speed_bonus"));
                list.add(vo);
            }
        } catch (SQLException e) {
            System.err.println("❌ 查詢背包裝備時發生 SQL 異常，原因: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.close(rs, ps, conn);
        }

        return list;
    }

    @Override
    public boolean insert(HeroEquipment heroEquip) {
        String sql = "INSERT INTO hero_equipment (hero_id, equipment_id, is_equipped) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DbUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, heroEquip.getHeroId());
            ps.setInt(2, heroEquip.getEquipmentId());
            ps.setBoolean(3, heroEquip.isEquipped());

            int rows = ps.executeUpdate();
            return rows > 0; // 影響行數大於 0 代表寫入成功
        } catch (SQLException e) {
            System.err.println("❌ 寫入角色掉落裝備時發生 SQL 異常，原因: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DbUtil.close(null, ps, conn);
        }
    }

    @Override
    public boolean updateEquippedStatus(int id, boolean isEquipped) {
        String sql = "UPDATE hero_equipment SET is_equipped = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DbUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setBoolean(1, isEquipped);
            ps.setInt(2, id);

            int rows = ps.executeUpdate();
            return rows > 0; // 影響行數大於 0 代表修改成功
        } catch (SQLException e) {
            System.err.println("❌ 更新背包裝備穿戴狀態時發生 SQL 異常，原因: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DbUtil.close(null, ps, conn);
        }
    }
}
