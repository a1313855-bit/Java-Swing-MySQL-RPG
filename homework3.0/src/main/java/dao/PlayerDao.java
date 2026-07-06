package dao;

import entity.Player;

/**
 * 玩家資料存取介面 (PlayerDao)
 * 定義與玩家帳號（player 資料表）相關的資料庫操作行為。
 */
public interface PlayerDao {
    
    /**
     * 新增玩家帳號 (用於註冊)
     * @param player 包含帳號、密碼、暱稱與信箱的玩家實體物件
     * @return boolean 新增成功返回 true，失敗返回 false
     */
    boolean insertPlayer(Player player);

    /**
     * 根據帳號查詢玩家 (用於登入驗證、檢查帳號是否重複)
     * @param username 登入帳號
     * @return Player 查詢到的玩家物件；若無此帳號則返回 null
     */
    Player selectByUsernameAndPassword(String username,String password);

    /**
     * 根據帳號查詢玩家 (僅用於檢查帳號是否已被註冊)
     * @param username 帳號
     * @return Player 查詢到的玩家物件；若無此帳號則返回 null
     */
    Player selectByUsername(String username);

    /**
     * 根據暱稱查詢玩家 (用於檢查暱稱是否已被註冊)
     * @param nickName 玩家暱稱
     * @return Player 查詢到的玩家物件；若無此暱稱則返回 null
     */
    Player selectBynickName(String nickName);

    /**
     * 根據信箱查詢玩家 (用於忘記密碼驗證)
     * @param mail 玩家註冊的信箱
     * @return Player 查詢到的玩家物件；若無此信箱則返回 null
     */
    Player selectByMail(String mail);

    /**
     * 根據玩家 ID 修改密碼 (用於重設密碼)
     * @param id 玩家唯一的 ID (主鍵)
     * @param newPassword 新密碼
     * @return boolean 修改成功返回 true，失敗返回 false
     */
    boolean updatePassword(int id, String newPassword);
}
