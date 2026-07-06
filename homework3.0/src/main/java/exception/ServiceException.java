package exception;

/**
 * 業務邏輯層自訂例外類別 (ServiceException)
 * 繼承自 RuntimeException (非受檢例外)。
 * 
 * 💡 您的選擇非常現代且專業！
 * 現代 Java 開發（如 Spring 框架）幾乎全面採用 RuntimeException。
 * 這樣可以避免在每個方法的宣告上都加上 "throws ServiceException"，
 * 讓程式碼更加乾淨，同時我們依然可以在 UI 層使用 try-catch 來捕捉它。
 */
public class ServiceException extends RuntimeException {
    
    /**
     * 傳入錯誤訊息的建構子
     * @param message 要顯示給玩家看的錯誤原因（例如：「帳號已重複！」）
     */
    public ServiceException(String message) {
        super(message);
    }
}
