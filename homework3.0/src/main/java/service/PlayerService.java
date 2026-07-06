package service;

import entity.Player;
import exception.ServiceException;

/**
 * 玩家業務邏輯介面 (PlayerService)
 * 定義遊戲中帳號管理相關的業務流程規格。
 */
public interface PlayerService {

    /**
     * 玩家登入驗證
     * @param username 帳號
     * @param password 密碼
     * @return Player 登入成功後回傳的玩家物件
     * @throws ServiceException 登入失敗時（如帳密錯誤），拋出包含原因的例外狀況
     */
    Player login(String username, String password) throws ServiceException;

    /**
     * 玩家註冊帳號
     * @param player 註冊資訊（包含帳號、密碼、暱稱、信箱）
     * @throws ServiceException 註冊失敗時（如欄位空白、資料重複），拋出包含原因的例外狀況
     */
    void register(Player player) throws ServiceException;

    /**
     * 忘記密碼 - 重設密碼
     * @param mail 註冊時的信箱
     * @param newPassword 新密碼
     * @param checkNewPassword 確認新密碼
     * @throws ServiceException 驗證失敗或更新失敗時（如信箱不存在、欄位空白、兩次密碼不一致），拋出包含原因的例外狀況
     */
    void changePassword(String mail, String newPassword, String checkNewPassword) throws ServiceException;
}
