package controller;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import exception.ServiceException;
import service.PlayerService;
import service.impl.PlayerServiceImpl;

/**
 * 重設密碼視窗類別 (ResetPasswordUI)
 * 提供玩家透過信箱驗證，並輸入新密碼及確認新密碼來重設帳號密碼。
 */
public class ResetPasswordUI extends JFrame {

    private JPanel contentPane;
    private JTextField mail;         // 信箱輸入框 (對應 mail)
    private JPasswordField password; // 新密碼輸入框 (對應 password)
    private JPasswordField newPassword; // 確認新密碼輸入框 (對應 newPassword)
    private JLabel errorMessage;     // 顯示錯誤原因的紅字 Label

    // 引入 Service 物件
    private PlayerService playerService = new PlayerServiceImpl();

    /**
     * 啟動程式進入點 (方便單獨測試重設密碼視窗)
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ResetPasswordUI frame = new ResetPasswordUI();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 建立重設密碼視窗
     */
    public ResetPasswordUI() {
        setTitle("RPG 放置遊戲 - 重設密碼");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // 寬度 450, 高度 330
        setBounds(100, 100, 450, 330);
        setLocationRelativeTo(null); // 視窗居中顯示
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null); // 絕對定位，方便 WindowBuilder 拖拉
        
        // 標題
        JLabel lblTitle = new JLabel("重設密碼");
        lblTitle.setFont(new Font("微軟正黑體", Font.BOLD, 22));
        lblTitle.setBounds(170, 15, 120, 30);
        contentPane.add(lblTitle);
        
        // 1. 信箱
        JLabel lblMail = new JLabel("電子信箱：");
        lblMail.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        lblMail.setBounds(70, 70, 80, 25);
        contentPane.add(lblMail);
        
        mail = new JTextField();
        mail.setBounds(170, 70, 180, 25);
        contentPane.add(mail);
        mail.setColumns(10);
        
        // 2. 新密碼
        JLabel lblPassword = new JLabel("新密碼：");
        lblPassword.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        lblPassword.setBounds(70, 120, 80, 25);
        contentPane.add(lblPassword);
        
        password = new JPasswordField();
        password.setBounds(170, 120, 180, 25);
        contentPane.add(password);
        
        // 3. 確認新密碼
        JLabel lblNewPassword = new JLabel("確認新密碼：");
        lblNewPassword.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        lblNewPassword.setBounds(70, 170, 90, 25);
        contentPane.add(lblNewPassword);
        
        newPassword = new JPasswordField();
        newPassword.setBounds(170, 170, 180, 25);
        contentPane.add(newPassword);
        
        // 4. 錯誤提示 Label
        errorMessage = new JLabel("");
        errorMessage.setForeground(Color.RED);
        errorMessage.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        errorMessage.setBounds(70, 205, 300, 20);
        contentPane.add(errorMessage);
        
        // 5. 確定按鈕 (原註冊按鈕，改為執行修改密碼)
        JButton btnSubmit = new JButton("確定");
        btnSubmit.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        btnSubmit.setBounds(100, 235, 90, 30);
        contentPane.add(btnSubmit);
        
        // 6. 返回登入按鈕
        JButton btnBack = new JButton("返回登入");
        btnBack.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        btnBack.setBounds(230, 235, 100, 30);
        contentPane.add(btnBack);
        
        // ==========================================
        // 事件監聽 (MouseClicked)
        // ==========================================
        
        // 確定按鈕點擊事件
        btnSubmit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 清除之前的錯誤訊息
                errorMessage.setText("");
                
                String Mail = mail.getText();
                String Password = new String(password.getPassword());
                String NewPassword = new String(newPassword.getPassword()); // 確認新密碼
                
                try {
                    // 💡 呼叫更新後的 Service 方法（傳入 3 個參數：信箱、新密碼、確認新密碼）
                    playerService.changePassword(Mail, Password, NewPassword);
                    
                    // 彈出提示視窗
                    javax.swing.JOptionPane.showMessageDialog(
                        ResetPasswordUI.this,
                        "重設成功!請至登入頁面登入帳號",
                        "系統提示",
                        javax.swing.JOptionPane.INFORMATION_MESSAGE
                    );
                    
                    // 重設成功後回到登入頁面
                    LoginFrame loginFrame = new LoginFrame();
                    loginFrame.setVisible(true);
                    dispose(); // 關閉目前視窗
                    
                } catch (ServiceException ex) {
                    // 將失敗原因顯示在紅字 Label
                    errorMessage.setText(ex.getMessage());
                }
            }
        });
        
        // 返回登入按鈕點擊事件
        btnBack.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 回到登入頁面
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
                dispose(); // 關閉目前視窗
            }
        });
    }
}
