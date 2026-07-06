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

import entity.Player;
import exception.ServiceException;
import service.PlayerService;
import service.impl.PlayerServiceImpl;

/**
 * 註冊視窗類別 (AddPlayerUI)
 * 使用 Java Swing 製作，提供玩家輸入帳號、密碼、暱稱、信箱進行註冊。
 */
public class AddPlayerUI extends JFrame {

    private JPanel contentPane;
    private JTextField nickName;   // 暱稱輸入框
    private JTextField username;   // 帳號輸入框
    private JPasswordField password; // 密碼輸入框
    private JTextField mail;       // 信箱輸入框
    private JLabel errorMessage;   // 顯示註冊失敗原因的紅字 Label

    // 引入 Service 物件
    private PlayerService playerService = new PlayerServiceImpl();

    /**
     * 啟動程式進入點 (方便單獨測試註冊視窗)
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    AddPlayerUI frame = new AddPlayerUI();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 建立註冊視窗
     */
    public AddPlayerUI() {
        setTitle("RPG 放置遊戲 - 註冊帳號");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // 寬度 450, 高度 380 (因為欄位較多，高度調高一點)
        setBounds(100, 100, 450, 380);
        setLocationRelativeTo(null); // 視窗居中顯示
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null); // 絕對定位，方便 WindowBuilder 拖拉
        
        // 標題
        JLabel lblTitle = new JLabel("創建新帳號");
        lblTitle.setFont(new Font("微軟正黑體", Font.BOLD, 22));
        lblTitle.setBounds(160, 15, 150, 30);
        contentPane.add(lblTitle);
        
        // 1. 暱稱
        JLabel lblNickName = new JLabel("遊戲暱稱：");
        lblNickName.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        lblNickName.setBounds(70, 70, 80, 25);
        contentPane.add(lblNickName);
        
        nickName = new JTextField();
        nickName.setBounds(160, 70, 180, 25);
        contentPane.add(nickName);
        nickName.setColumns(10);
        
        // 2. 帳號
        JLabel lblUsername = new JLabel("登入帳號：");
        lblUsername.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        lblUsername.setBounds(70, 120, 80, 25);
        contentPane.add(lblUsername);
        
        username = new JTextField();
        username.setBounds(160, 120, 180, 25);
        contentPane.add(username);
        username.setColumns(10);
        
        // 3. 密碼
        JLabel lblPassword = new JLabel("登入密碼：");
        lblPassword.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        lblPassword.setBounds(70, 170, 80, 25);
        contentPane.add(lblPassword);
        
        password = new JPasswordField();
        password.setBounds(160, 170, 180, 25);
        contentPane.add(password);
        
        // 4. 信箱
        JLabel lblMail = new JLabel("電子信箱：");
        lblMail.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        lblMail.setBounds(70, 220, 80, 25);
        contentPane.add(lblMail);
        
        mail = new JTextField();
        mail.setBounds(160, 220, 180, 25);
        contentPane.add(mail);
        mail.setColumns(10);
        
        // 5. 錯誤提示訊息 Label
        errorMessage = new JLabel("");
        errorMessage.setForeground(Color.RED);
        errorMessage.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        errorMessage.setBounds(70, 255, 300, 20);
        contentPane.add(errorMessage);
        
        // 6. 註冊按鈕
        JButton btnRegister = new JButton("註冊");
        btnRegister.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        btnRegister.setBounds(100, 285, 90, 30);
        contentPane.add(btnRegister);
        
        // 7. 返回登入按鈕 (方便玩家取消註冊回到登入頁)
        JButton btnBack = new JButton("返回登入");
        btnBack.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        btnBack.setBounds(230, 285, 100, 30);
        contentPane.add(btnBack);
        
        // ==========================================
        // 事件監聽 (MouseClicked)
        // ==========================================
        
        // 註冊按鈕的點擊事件
        btnRegister.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 清除之前的錯誤訊息
                errorMessage.setText("");
                
                // 抓取輸入框的文字
                String NickName = nickName.getText();
                String Username = username.getText();
                String Password = new String(password.getPassword()); // 確保密碼安全
                String Mail = mail.getText();
                
                try {
                    // 💡 修正點：因為 Service 的 register 方法接收的是 Player 物件，
                    // 我們需要先將四個欄位的值打包封裝成 Player 物件，再傳入 Service！
                    Player newPlayer = new Player(Username, Password, NickName, Mail);
                    playerService.register(newPlayer);
                    
                    // 註冊成功彈出提示
                    javax.swing.JOptionPane.showMessageDialog(
                        AddPlayerUI.this,
                        "註冊成功!請至登入頁面登入帳號",
                        "系統提示",
                        javax.swing.JOptionPane.INFORMATION_MESSAGE
                    );
                    
                    // 💡 註冊成功後，自動幫玩家跳轉回登入視窗！
                    LoginFrame loginFrame = new LoginFrame();
                    loginFrame.setVisible(true);
                    dispose(); // 關閉目前註冊視窗
                    
                } catch (ServiceException ex) {
                    // 失敗時在紅字 Label 顯示原因
                    errorMessage.setText(ex.getMessage());
                }
            }
        });
        
        // 返回按鈕的點擊事件
        btnBack.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 回到登入頁面
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
                dispose(); // 關閉目前註冊視窗
            }
        });
    }
}
