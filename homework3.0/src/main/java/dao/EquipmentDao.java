package dao;

import java.util.List;
import entity.Equipment;

/**
 * 裝備範本資料存取介面 (EquipmentDao)
 * 💡 負責讀取預設的 12 件靜態裝備範本。
 */
public interface EquipmentDao {

    /**
     * 根據品級 (Tier) 查詢該品級的所有裝備範本
     * @param tier 品級 (1: 基礎, 2: 中級, 3: 高級)
     * @return List<Equipment> 該品級的裝備清單
     */
    List<Equipment> selectByTier(int tier);
}
