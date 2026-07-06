package util;

import entity.Player;
import exception.ServiceException;
import service.PlayerService;
import service.impl.PlayerServiceImpl;

/**
 * 業務邏輯層 (Service) 測試類別
 * 用來展示及驗證自訂 Exception (ServiceException) 的防呆與阻擋機制。
 */
public class ServiceTest {
    public static void main(String[] args) {
        PlayerService service = new PlayerServiceImpl();
        
        System.out.println("======= 開始測試 PlayerServiceImpl 業務邏輯與 Exception 機制 =======");

        // 準備測試用帳密
        long timestamp = System.currentTimeMillis() / 1000;
        String testUser = "service_user_" + timestamp;
        String testMail = "service_user_" + timestamp + "@rpg.com";
        String testNick = "神選勇者" + timestamp;

        // -----------------------------------------------------
        // 測試 1：註冊一個正常的帳號
        // -----------------------------------------------------
        try {
            System.out.println("\n[測試 1] 註冊正常帳號：" + testUser);
            Player p = new Player(testUser, "pwd123", testNick, testMail);
            service.register(p);
            System.out.println("  -> 狀態：【成功】順利完成註冊！");
        } catch (ServiceException e) {
            System.out.println("  -> 狀態：【失敗】原因：" + e.getMessage());
        }

        // -----------------------------------------------------
        // 測試 2：用相同的帳號重複註冊（預期會被 Service 擋下並丟出 Exception）
        // -----------------------------------------------------
        try {
            System.out.println("\n[測試 2] 嘗試重複註冊帳號：" + testUser);
            // 帳號相同，其他資料不同
            Player duplicate = new Player(testUser, "diffPass", "另一個暱稱", "diff@mail.com");
            service.register(duplicate);
            System.out.println("  -> 狀態：【成功】（這是不對的！重複帳號不應被允許）");
        } catch (ServiceException e) {
            // 我們會在 catch 區塊中直接接收並印出 Service 定義好的中文錯誤原因
            System.out.println("  -> 狀態：【被 Service 阻擋成功】原因：" + e.getMessage());
        }

        // -----------------------------------------------------
        // 測試 3：登入測試 - 使用錯誤的密碼
        // -----------------------------------------------------
        try {
            System.out.println("\n[測試 3] 嘗試用錯誤密碼登入：" + testUser);
            service.login(testUser, "wrongPassword123");
            System.out.println("  -> 狀態：【成功】（這是不對的！密碼錯了不應登入）");
        } catch (ServiceException e) {
            System.out.println("  -> 狀態：【登入失敗，阻擋成功】原因：" + e.getMessage());
        }

        // -----------------------------------------------------
        // 測試 4：忘記密碼 - 輸入不存在的信箱重設密碼
        // -----------------------------------------------------
        try {
            System.out.println("\n[測試 4] 使用不存在的信箱重設密碼...");
            service.changePassword("not_exists_email@game.com", "newPassword789", "newPassword789");
            System.out.println("  -> 狀態：【成功】（這是不對的！信箱不存在不能重設）");
        } catch (ServiceException e) {
            System.out.println("  -> 狀態：【重設失敗，阻擋成功】原因：" + e.getMessage());
        }

        System.out.println("\n=======================================================");
    }
}
