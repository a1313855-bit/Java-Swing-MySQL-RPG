package service.impl;

import dao.PlayerDao;
import dao.impl.PlayerDaoImpl;
import entity.Player;
import exception.ServiceException;
import service.PlayerService;

/**
 * 玩家業務邏輯實作類別 (PlayerServiceImpl)
 * 負責處理各項帳號驗證與防呆邏輯，並呼叫 DAO 與資料庫進行互動。
 */
public class PlayerServiceImpl implements PlayerService {

    // 建立與 DAO 層的關聯（在沒有 Spring 框架的 Java SE 環境中，手動 new 出實作物件）
    private PlayerDao playerDao = new PlayerDaoImpl();

    @Override
    public Player login(String username, String password) throws ServiceException {
        // 1. 防呆驗證：帳號密碼不能為空白
        if (username == null || username.trim().isEmpty()) {
            throw new ServiceException("登入失敗：帳號不能為空！");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new ServiceException("登入失敗：密碼不能為空！");
        }

        // 2. 呼叫 DAO 進行帳密驗證
        Player player = playerDao.selectByUsernameAndPassword(username.trim(), password);
        
        // 3. 判斷驗證結果
        if (player == null) {
            throw new ServiceException("登入失敗：帳號或密碼錯誤！");
        }

        return player;
    }

    @Override
    public void register(Player player) throws ServiceException {
        // 1. 基本防呆驗證：所有註冊欄位皆不能為空
        if (player.getUsername() == null || player.getUsername().trim().isEmpty()) {
            throw new ServiceException("註冊失敗：帳號欄位不能為空！");
        }
        if (player.getPassword() == null || player.getPassword().trim().isEmpty()) {
            throw new ServiceException("註冊失敗：密碼欄位不能為空！");
        }
        if (player.getNickName() == null || player.getNickName().trim().isEmpty()) {
            throw new ServiceException("註冊失敗：暱稱欄位不能為空！");
        }
        if (player.getMail() == null || player.getMail().trim().isEmpty()) {
            throw new ServiceException("註冊失敗：信箱欄位不能為空！");
        }

        // 2. 重複性檢查 (使用剛剛在 DAO 新增的查詢方法)
        
        // (A) 檢查帳號是否重複
        if (playerDao.selectByUsername(player.getUsername().trim()) != null) {
            throw new ServiceException("註冊失敗：此帳號已被註冊！");
        }
        
        // (B) 檢查暱稱是否重複
        if (playerDao.selectBynickName(player.getNickName().trim()) != null) {
            throw new ServiceException("註冊失敗：此名稱已被註冊！");
        }
        
        // (C) 檢查信箱是否重複
        if (playerDao.selectByMail(player.getMail().trim()) != null) {
            throw new ServiceException("註冊失敗：此信箱已被註冊！");
        }

        // 3. 如果檢查全部通過，才執行註冊寫入資料庫
        boolean isSuccess = playerDao.insertPlayer(player);
        if (!isSuccess) {
            throw new ServiceException("註冊失敗：資料庫寫入異常，請聯繫管理員！");
        }
    }

    @Override
    public void changePassword(String mail, String newPassword, String checkNewPassword) throws ServiceException {
        // 1. 基本防呆：信箱、新密碼、確認密碼不能為空
        if (mail == null || mail.trim().isEmpty()) {
            throw new ServiceException("重設失敗：電子信箱不能為空！");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new ServiceException("重設失敗：新密碼不能為空！");
        }
        if (checkNewPassword == null || checkNewPassword.trim().isEmpty()) {
            throw new ServiceException("重設失敗：確認新密碼不能為空！");
        }

        // 2. 驗證新密碼與確認密碼是否一致 (💡 這裡當不相同時拋出 Exception)
        if (!newPassword.equals(checkNewPassword)) {
            throw new ServiceException("重設失敗：確認密碼與新密碼不相同！");
        }

        // 3. 驗證信箱是否存在
        Player player = playerDao.selectByMail(mail.trim());
        if (player == null) {
            throw new ServiceException("重設失敗：該信箱尚未註冊任何帳號！");
        }

        // 4. 透過查出來的 ID 來修改密碼
        boolean isSuccess = playerDao.updatePassword(player.getId(), newPassword);
        if (!isSuccess) {
            throw new ServiceException("重設失敗：資料庫密碼更新異常！");
        }
    }
}
