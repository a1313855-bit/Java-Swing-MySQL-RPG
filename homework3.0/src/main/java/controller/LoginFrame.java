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
import java.awt.Cursor;          // 💡 [新增 AWT 元件] 用於管理滑鼠游標的樣式 (如手型、沙漏、指標等)
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;  // 💡 [新增事件元件] 滑鼠事件適配器，讓我們只實作需要用的滑鼠事件方法
import java.awt.event.MouseEvent;    // 💡 [新增事件元件] 封裝滑鼠事件的詳細資訊 (如座標、點擊類型)

import entity.Player;
import exception.ServiceException;
import service.PlayerService;
import service.impl.PlayerServiceImpl;

/**
 * 登入視窗類別 (LoginFrame)
 * 使用 Java Swing 製作，可直接使用 Eclipse WindowBuilder 進行視覺化設計 (Design 模式)。
 */
public class LoginFrame extends JFrame {

    private JPanel contentPane;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JLabel lblErrorMessage; // 用來顯示錯誤訊息的紅字 Label

    // 💡 引入 Service 物件 (我們約定的第一點：宣告並實例化)
    private PlayerService playerService = new PlayerServiceImpl();

    /**
     * 啟動程式的進入點 (方便單獨測試此視窗)
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    LoginFrame frame = new LoginFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 建立登入視窗
     */
    public LoginFrame() {
        // 設定視窗標題與關閉行為
        setTitle("RPG 放置遊戲 - 登入系統");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // 設定視窗寬高：寬 450, 高 300
        setBounds(100, 100, 450, 300);
        // 設定視窗開啟時自動居中顯示
        setLocationRelativeTo(null);
        
        // 初始化面板容器
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        // 使用絕對定位 (Null Layout)，最適合 WindowBuilder 進行拖拉設計
        contentPane.setLayout(null);
        
        // 標題標籤
        JLabel lblTitle = new JLabel("RPG 放置遊戲");
        lblTitle.setFont(new Font("微軟正黑體", Font.BOLD, 22));
        lblTitle.setBounds(140, 20, 200, 40);
        contentPane.add(lblTitle);
        
        // 帳號標籤
        JLabel lblUsername = new JLabel("帳號：");
        lblUsername.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        lblUsername.setBounds(80, 85, 60, 25);
        contentPane.add(lblUsername);
        
        // 帳號輸入框
        txtUsername = new JTextField();
        txtUsername.setBounds(150, 85, 180, 25);
        contentPane.add(txtUsername);
        txtUsername.setColumns(10);
        
        // 密碼標籤
        JLabel lblPassword = new JLabel("密碼：");
        lblPassword.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        lblPassword.setBounds(80, 135, 60, 25);
        contentPane.add(lblPassword);
        
        // 密碼輸入框
        txtPassword = new JPasswordField();
        txtPassword.setBounds(150, 135, 180, 25);
        contentPane.add(txtPassword);
        
        // 💡 錯誤提示標籤 (我們約定的第三點：新增一個紅色 Label，起初內容為空)
        lblErrorMessage = new JLabel("");
        lblErrorMessage.setForeground(Color.RED);
        lblErrorMessage.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        lblErrorMessage.setBounds(80, 165, 300, 20);
        contentPane.add(lblErrorMessage);
        
        // 登入按鈕
        JButton btnLogin = new JButton("登入");
        btnLogin.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        btnLogin.setBounds(100, 190, 90, 30);
        contentPane.add(btnLogin);
        
        // 註冊按鈕
        JButton btnRegister = new JButton("註冊帳號");
        btnRegister.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        btnRegister.setBounds(230, 190, 100, 30);
        contentPane.add(btnRegister);

        // 忘記密碼超連結標籤 (我們約定的第三點：滑鼠移入變藍、移出變黑、點擊進入重設頁)
        JLabel resetPassword = new JLabel("忘記密碼?");
        resetPassword.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        resetPassword.setBounds(190, 235, 70, 20);
        resetPassword.setForeground(Color.BLACK);
        
        /* 
         * 💡 游標樣式設定解說：
         * 1. java.awt.Cursor 是 Java AWT 套件中用來控制滑鼠指標外觀的類別。
         * 2. Cursor.getPredefinedCursor(...) 可以取得 Java 內建預設的幾種指標圖示。
         * 3. Cursor.HAND_CURSOR 代表「手型游標」（通常出現在網頁超連結上），讓玩家直覺知道這是一個可點擊的連結。
         */
        resetPassword.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        contentPane.add(resetPassword);
        
        // ==========================================
        // 按鈕事件監聽器 (Listeners)
        // ==========================================

        // 監聽忘記密碼超連結事件
        resetPassword.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                resetPassword.setForeground(Color.BLUE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                resetPassword.setForeground(Color.BLACK);
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                ResetPasswordUI resetPasswordUi = new ResetPasswordUI();
                resetPasswordUi.setVisible(true);
                dispose(); // 關閉目前登入視窗
            }
        });
        
        // 監聽登入按鈕點擊事件 (我們約定的第二點：用 try-catch 呼叫 Service 的登入方法，改回您熟悉的 MouseClicked)
        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // 每次點擊先清除上一次的錯誤訊息
                lblErrorMessage.setText("");
                
                String username = txtUsername.getText();
                String password = new String(txtPassword.getPassword()); // 確保密碼安全性的字元陣列轉換
                
                try {
                    // 呼叫 Service 層驗證登入
                    Player player = playerService.login(username, password);
                    
                    // 💡 登入成功：彈出歡迎視窗 (即選項 A)
                    javax.swing.JOptionPane.showMessageDialog(
                        LoginFrame.this,
                        "登入成功！歡迎回來，" + player.getNickName(),
                        "系統提示",
                        javax.swing.JOptionPane.INFORMATION_MESSAGE
                    );
                    
                    // 💡【核心機制：跨視窗資料傳遞（Constructor Data Passing）】
                    // 原理：在建立下一個視窗 ChooseHeroUI 的實體時，將登入成功後獲得的玩家 ID (player.getId()) 作為建構子參數傳入。
                    // 這樣，下一個視窗的建構子就能接收到這個 ID，並去資料庫撈取對應玩家的角色列表，完成跨畫面資料串接！
                    ChooseHeroUI chooseHeroUi = new ChooseHeroUI(player.getId());
                    chooseHeroUi.setVisible(true);
                    dispose(); // 關閉目前登入視窗
                    
                } catch (ServiceException ex) {
                    // 💡 登入失敗：在畫面的 Label 顯示紅字 (即選項 B)
                    lblErrorMessage.setText(ex.getMessage());
                }
            }
        });
        
        // 監聽註冊按鈕點擊事件 (改為 MouseClicked，跳轉至註冊視窗)
        btnRegister.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                AddPlayerUI addPlayerUi = new AddPlayerUI();
                addPlayerUi.setVisible(true);
                dispose(); // 關閉目前登入視窗
            }
        });
    }
}
