package dao;

import java.util.List;
import entity.HeroEquipment;
import vo.HeroEquipmentVO;

/**
 * 角色背包裝備資料存取介面 (HeroEquipmentDao)
 * 💡 負責處理角色背包的裝備載入、新增掉落裝備、以及修改裝備穿戴狀態。
 */
public interface HeroEquipmentDao {

    /**
     * 根據角色 ID 查詢該角色的所有背包裝備明細 (結合範本屬性)
     * 💡 讀取自資料庫檢視表 `v_hero_equipment`
     * @param heroId 角色 ID
     * @return List<HeroEquipmentVO> 裝備明細列表
     */
    List<HeroEquipmentVO> selectByHeroId(int heroId);

    /**
     * 將怪物掉落的新裝備寫入該角色的背包中
     * 💡 寫入 `hero_equipment` 表
     * @param heroEquip 角色背包裝備關聯實體
     * @return boolean 是否寫入成功
     */
    boolean insert(HeroEquipment heroEquip);

    /**
     * 更新特定背包裝備的穿戴狀態 (穿上/脫下)
     * 💡 修改 `hero_equipment` 表
     * @param id 該背包裝備的唯一主鍵 ID (流水號)
     * @param isEquipped 是否穿戴 (true:穿上, false:放回背包)
     * @return boolean 是否更新成功
     */
    boolean updateEquippedStatus(int id, boolean isEquipped);
}
