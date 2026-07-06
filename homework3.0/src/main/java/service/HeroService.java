package service;

import entity.Hero;
import entity.HeroStats;
import vo.HeroVO;
import exception.ServiceException;
import java.util.List;

/**
 * 角色業務邏輯介面 (HeroService)
 * 💡 專注於目前「選擇角色大廳」視窗所需的核心業務邏輯。
 */
public interface HeroService {

    /**
     * 查詢指定玩家帳號底下的所有角色
     * @param playerId 玩家帳號唯一的識別 ID (主鍵)
     * @return List<HeroVO> 該玩家擁有的所有角色整合明細清單
     * @throws ServiceException 當查詢發生異常時拋出
     */
    List<HeroVO> showPlayerHeroById(int playerId) throws ServiceException;

    /**
     * 刪除指定 ID 的角色
     * 💡 採用您簡約直接的設計流：不強制要求輸入名字確認，點擊直接刪除。
     * @param heroId 準備被刪除的角色 ID
     * @return boolean 是否刪除成功
     * @throws ServiceException 當刪除失敗時拋出
     */
    boolean deletePlayerHero(int heroId) throws ServiceException;

    /**
     * 創建新角色 (含防呆驗證：名稱為空、同玩家名稱重複、點數是否分配完全)
     * @param hero 包含玩家 ID、暱稱、性別與頭像檔名的基本物件
     * @param stats 包含初始血量、攻擊力、防禦力、速度與暴擊率的戰鬥數值物件
     * @throws ServiceException 當防呆驗證失敗或寫入資料庫失敗時拋出中文錯誤原因
     */
    void createHero(Hero hero, HeroStats stats) throws ServiceException;
}
