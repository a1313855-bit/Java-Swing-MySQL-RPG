package dao;

import java.util.List;
import entity.Monster;

/**
 * 怪物資料存取介面 (MonsterDao)
 * 💡 負責與 MySQL 資料庫中的 `monster` 表進行交互。
 */
public interface MonsterDao {

    /**
     * 根據地圖 ID 查詢該地圖的所有怪物列表
     * @param mapId 地圖 ID (1:新手草原, 2:幽暗森林, 3:烈焰火山)
     * @return List<Monster> 怪物列表 (若地圖無怪物則回傳空清單)
     */
    List<Monster> selectByMapId(int mapId);
}
