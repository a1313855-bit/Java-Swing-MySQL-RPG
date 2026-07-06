package dao;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.impl.PlayerDaoImpl;
import entity.Player;

/**
 * 玩家資料存取層的單元測試類別 (PlayerDaoTest)
 * 💡 這是業界標準的測試寫法，放置於 src/test/java 目錄下，與正式程式碼分離。
 */
public class PlayerDaoTest {

    private PlayerDao playerDao;

    /**
     * 每個測試方法執行前都會先執行的初始化方法
     */
    @BeforeEach
    public void setUp() {
        // 實例化我們要測試的對象
        playerDao = new PlayerDaoImpl();
    }

    /**
     * 測試玩家帳號的「新增」與「查詢」功能
     * 💡 業界會使用 Assertions (斷言) 來自動判斷程式結果是否正確，而不需要用肉眼在 Console 看。
     */
    @Test
    public void testInsertAndSelectPlayer() {
        // 1. 準備隨機的測試資料 (加上時間戳記以防 UNIQUE 重複限制)
        String testUsername = "junit_user_" + (System.currentTimeMillis() / 1000);
        Player newPlayer = new Player(testUsername, "junitPass", "JUnit勇者", "junit@test.com");

        // 2. 測試新增玩家是否成功
        boolean insertResult = playerDao.insertPlayer(newPlayer);
        // 💡 斷言：insertResult 必須是 true，否則測試失敗
        assertTrue(insertResult, "新增玩家應該要成功");

        // 3. 測試根據帳密查詢玩家
        Player queriedPlayer = playerDao.selectByUsernameAndPassword(testUsername, "junitPass");
        // 💡 斷言：查詢結果不應該為 null
        assertNotNull(queriedPlayer, "查詢出來的玩家物件不應該為 null");
        
        // 💡 斷言：查詢出來的各個欄位內容，必須與我們剛剛新增的資料完全一致
        assertEquals(testUsername, queriedPlayer.getUsername(), "查詢到的帳號應與新增時一致");
        assertEquals("JUnit勇者", queriedPlayer.getNickName(), "查詢到的暱稱應與新增時一致");
        assertEquals("junit@test.com", queriedPlayer.getMail(), "查詢到的信箱應與新增時一致");

        // 4. 測試「使用錯誤密碼」查詢，預期應該要查不到（返回 null）
        Player wrongPasswordPlayer = playerDao.selectByUsernameAndPassword(testUsername, "wrongPassword123");
        // 💡 斷言：使用錯誤密碼時，回傳值必須為 null
        assertNull(wrongPasswordPlayer, "使用錯誤密碼查詢，結果應該為 null");
    }
}
