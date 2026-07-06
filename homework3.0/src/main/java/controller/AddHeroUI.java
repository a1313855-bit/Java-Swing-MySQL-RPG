package controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * 創建角色視窗 (AddHeroUI)
 * 💡 這是您所設計的版面！
 * 包含：角色圖片預覽、名稱輸入、男女單選按鈕、剩餘屬性點配點機制（HP/ATK/DEF/Speed）。
 */
public class AddHeroUI extends JFrame {

    private JPanel contentPane;
    private JTextField txtName;        // 角色名字輸入框
    private JRadioButton radioMale;    // 男性單選按鈕
    private JRadioButton radioFemale;  // 女性單選按鈕
    private ButtonGroup genderGroup;   // 性別按鈕群組 (確保男女互斥，只能選一個)
    
    // 圖片預覽
    private JLabel lblImagePreview;
    
    // 屬性配點數值 Label
    private JLabel lblPointsVal;       // 剩餘點數
    private JLabel lblHpVal;           // 生命值
    private JLabel lblAtkVal;          // 攻擊力
    private JLabel lblDefVal;          // 防禦力
    private JLabel lblSpeedVal;        // 速度
    
    // 配點控制按鈕
    private JButton btnHpMinus, btnHpPlus;
    private JButton btnAtkMinus, btnAtkPlus;
    private JButton btnDefMinus, btnDefPlus;
    private JButton btnSpeedMinus, btnSpeedPlus;
    
    // 創建與返回按鈕
    private JButton btnCreate;
    private JButton btnCancel;
    
    // 錯誤訊息提示 Label
    private JLabel lblErrorMessage;

    // --- 遊戲數據狀態變數 ---
    private int remainingPoints = 5;   // 初始配點點數
    
    // 各屬性分配的點數 (一開始都是 0 點，玩家點擊 + 才會增加)
    private int hpPoints = 0;
    private int atkPoints = 0;
    private int defPoints = 0;
    private int speedPoints = 0;
    
    // 各屬性的基礎值 (根據您的設定)
    private static final int BASE_HP = 100;
    private static final int BASE_ATK = 5;
    private static final int BASE_DEF = 5;
    private static final int BASE_SPEED = 5;
    
    // 換算比例
    private static final int HP_PER_POINT = 15; // 1 點血量點數 = 15 滴血

    // 運行時所需的玩家數據
    private int playerId = 1;          // 預設玩家 ID
    private ChooseHeroUI parentFrame;  // 父視窗引用，用於返回大廳

    /**
     * 💡 預設無參數建構子 (供 WindowBuilder 視覺化編輯器解析使用)
     */
    public AddHeroUI() {
        setTitle("RPG 放置遊戲 - 創建新角色");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 420); // 寬 600, 高 420，與 ChooseHeroUI 保持一致比例
        setLocationRelativeTo(null);   // 視窗置中
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // ==========================================
        // 左側：角色頭像預覽區 (JPanel)
        // ==========================================
        JPanel imgPanel = new JPanel();
        imgPanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
        imgPanel.setBounds(30, 30, 200, 260); // 寬 200, 高 260 的圖片容器
        contentPane.add(imgPanel);
        imgPanel.setLayout(null);

        lblImagePreview = new JLabel("請選擇性別");
        lblImagePreview.setBounds(10, 10, 180, 240);
        lblImagePreview.setHorizontalAlignment(JLabel.CENTER);
        imgPanel.add(lblImagePreview);

        // ==========================================
        // 右側：設定與配點區
        // ==========================================
        
        // 1. 角色名稱
        JLabel lblName = new JLabel("角色名稱：");
        lblName.setFont(new Font("微軟正黑體", Font.BOLD, 14));
        lblName.setBounds(260, 30, 80, 25);
        contentPane.add(lblName);

        txtName = new JTextField();
        txtName.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        txtName.setBounds(350, 30, 180, 25);
        contentPane.add(txtName);
        txtName.setColumns(10);

        // 2. 角色性別
        JLabel lblGender = new JLabel("角色性別：");
        lblGender.setFont(new Font("微軟正黑體", Font.BOLD, 14));
        lblGender.setBounds(260, 70, 80, 25);
        contentPane.add(lblGender);

        radioMale = new JRadioButton("男");
        radioMale.setSelected(true); // 預設選中男
        radioMale.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        radioMale.setBounds(350, 70, 70, 25);
        contentPane.add(radioMale);

        radioFemale = new JRadioButton("女");
        radioFemale.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        radioFemale.setBounds(430, 70, 70, 25);
        contentPane.add(radioFemale);

        // 💡 關鍵：必須將兩個單選按鈕加進同一個 ButtonGroup，才能達到「二選一」互斥的效果
        genderGroup = new ButtonGroup();
        genderGroup.add(radioMale);
        genderGroup.add(radioFemale);

        // 3. 初始配點標題與剩餘點數
        JLabel lblPointsTitle = new JLabel("剩餘屬性點：");
        lblPointsTitle.setFont(new Font("微軟正黑體", Font.BOLD, 14));
        lblPointsTitle.setBounds(260, 115, 90, 25);
        contentPane.add(lblPointsTitle);

        lblPointsVal = new JLabel("5");
        lblPointsVal.setForeground(new Color(40, 167, 69)); // 綠色字體突顯剩餘點數
        lblPointsVal.setFont(new Font("微軟正黑體", Font.BOLD, 16));
        lblPointsVal.setBounds(350, 115, 50, 25);
        contentPane.add(lblPointsVal);

        // --- 屬性 1: 血量 (HP) ---
        btnHpPlus = new JButton("+");
        btnHpPlus.setBounds(260, 150, 45, 25);
        contentPane.add(btnHpPlus);

        JLabel lblHp = new JLabel("血量：");
        lblHp.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        lblHp.setBounds(315, 150, 60, 25);
        contentPane.add(lblHp);

        lblHpVal = new JLabel("100");
        lblHpVal.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        lblHpVal.setBounds(395, 150, 50, 25);
        contentPane.add(lblHpVal);

        btnHpMinus = new JButton("-");
        btnHpMinus.setBounds(450, 150, 45, 25);
        contentPane.add(btnHpMinus);

        // --- 屬性 2: 攻擊力 (ATK) ---
        btnAtkPlus = new JButton("+");
        btnAtkPlus.setBounds(260, 185, 45, 25);
        contentPane.add(btnAtkPlus);

        JLabel lblAtk = new JLabel("攻擊力：");
        lblAtk.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        lblAtk.setBounds(315, 185, 60, 25);
        contentPane.add(lblAtk);

        lblAtkVal = new JLabel("5");
        lblAtkVal.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        lblAtkVal.setBounds(395, 185, 50, 25);
        contentPane.add(lblAtkVal);

        btnAtkMinus = new JButton("-");
        btnAtkMinus.setBounds(450, 185, 45, 25);
        contentPane.add(btnAtkMinus);

        // --- 屬性 3: 防禦力 (DEF) ---
        btnDefPlus = new JButton("+");
        btnDefPlus.setBounds(260, 220, 45, 25);
        contentPane.add(btnDefPlus);

        JLabel lblDef = new JLabel("防禦力：");
        lblDef.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        lblDef.setBounds(315, 220, 60, 25);
        contentPane.add(lblDef);

        lblDefVal = new JLabel("5");
        lblDefVal.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        lblDefVal.setBounds(395, 220, 50, 25);
        contentPane.add(lblDefVal);

        btnDefMinus = new JButton("-");
        btnDefMinus.setBounds(450, 220, 45, 25);
        contentPane.add(btnDefMinus);

        // --- 屬性 4: 攻擊速度 (Speed) ---
        btnSpeedPlus = new JButton("+");
        btnSpeedPlus.setBounds(260, 255, 45, 25);
        contentPane.add(btnSpeedPlus);

        JLabel lblSpeed = new JLabel("攻擊速度：");
        lblSpeed.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        lblSpeed.setBounds(315, 255, 70, 25);
        contentPane.add(lblSpeed);

        lblSpeedVal = new JLabel("5");
        lblSpeedVal.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        lblSpeedVal.setBounds(395, 255, 50, 25);
        contentPane.add(lblSpeedVal);

        btnSpeedMinus = new JButton("-");
        btnSpeedMinus.setBounds(450, 255, 45, 25);
        contentPane.add(btnSpeedMinus);

        // ==========================================
        // 下方：動作按鈕區
        // ==========================================
        btnCreate = new JButton("創建角色");
        btnCreate.setFont(new Font("微軟正黑體", Font.BOLD, 14));
        btnCreate.setBounds(170, 315, 100, 35);
        contentPane.add(btnCreate);

        btnCancel = new JButton("返回大廳");
        btnCancel.setFont(new Font("微軟正黑體", Font.BOLD, 14));
        btnCancel.setBounds(300, 315, 100, 35);
        contentPane.add(btnCancel);

        // 錯誤訊息 Label (Y 軸設在按鈕上方 285)
        lblErrorMessage = new JLabel("");
        lblErrorMessage.setForeground(new Color(220, 53, 69)); // 紅色字體
        lblErrorMessage.setFont(new Font("微軟正黑體", Font.BOLD, 12));
        lblErrorMessage.setBounds(260, 285, 300, 25);
        contentPane.add(lblErrorMessage);

        // ==========================================
        // 💡 事件監聽器配置 (Event Listeners)
        // ==========================================

        // 1. 性別單選按鈕切換時，動態刷新左邊的圖片預覽
        ActionListener genderListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateAvatarPreview();
            }
        };
        radioMale.addActionListener(genderListener);
        radioFemale.addActionListener(genderListener);

        // 2. 配點按鈕事件串接
        // --- HP 配點 ---
        btnHpPlus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (remainingPoints > 0) {
                    hpPoints++;
                    remainingPoints--;
                    updatePointsUI();
                }
            }
        });
        btnHpMinus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (hpPoints > 0) {
                    hpPoints--;
                    remainingPoints++;
                    updatePointsUI();
                }
            }
        });

        // --- ATK 配點 ---
        btnAtkPlus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (remainingPoints > 0) {
                    atkPoints++;
                    remainingPoints--;
                    updatePointsUI();
                }
            }
        });
        btnAtkMinus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (atkPoints > 0) {
                    atkPoints--;
                    remainingPoints++;
                    updatePointsUI();
                }
            }
        });

        // --- DEF 配點 ---
        btnDefPlus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (remainingPoints > 0) {
                    defPoints++;
                    remainingPoints--;
                    updatePointsUI();
                }
            }
        });
        btnDefMinus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (defPoints > 0) {
                    defPoints--;
                    remainingPoints++;
                    updatePointsUI();
                }
            }
        });

        // --- Speed 配點 ---
        btnSpeedPlus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (remainingPoints > 0) {
                    speedPoints++;
                    remainingPoints--;
                    updatePointsUI();
                }
            }
        });
        btnSpeedMinus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (speedPoints > 0) {
                    speedPoints--;
                    remainingPoints++;
                    updatePointsUI();
                }
            }
        });

        // 3. 返回大廳按鈕點擊
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnToLobby();
            }
        });

        // 4. 創建角色按鈕點擊 (呼叫 Service 層進行資料庫寫入)
        btnCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 💡 每次點擊先清空先前的錯誤訊息
                lblErrorMessage.setText("");
                
                // 💡 第一道防線：UI 層的點數驗證
                // 檢查是否還有剩餘點數未分配完畢
                if (remainingPoints > 0) {
                    lblErrorMessage.setText("創建失敗：還有剩餘的屬性點未分配！"); // 統一的輸出訊息
                    return; // 阻擋繼續執行
                }
                
                // 讀取 UI 輸入資料
                String name = txtName.getText();
                String gender = radioMale.isSelected() ? "男性" : "女性";
                String imgName = radioMale.isSelected() ? "male_hero.jpg" : "female_hero.jpg";
                
                // 封裝 Hero 基本實體 (playerId 是登入玩家的 ID，由 ChooseHeroUI 傳遞進來)
                entity.Hero hero = new entity.Hero(playerId, name, gender, imgName);
                
                // 計算分配後的實際屬性數值
                int finalHp = BASE_HP + hpPoints * HP_PER_POINT;
                int finalAtk = BASE_ATK + atkPoints;
                int finalDef = BASE_DEF + defPoints;
                int finalSpeed = BASE_SPEED + speedPoints;
                
                // 封裝 HeroStats 實體 (heroId 暫設為 -1，寫入時會由 DAO 中的交易生成並替換)
                entity.HeroStats stats = new entity.HeroStats(-1, 1, 0, finalHp, finalAtk, finalDef, finalSpeed);
                
                try {
                    // 呼叫邏輯層進行防呆與寫入
                    service.HeroService heroService = new service.impl.HeroServiceImpl();
                    heroService.createHero(hero, stats);
                    
                    // 提示創建成功
                    javax.swing.JOptionPane.showMessageDialog(
                        AddHeroUI.this,
                        "角色「" + name + "」創建成功！",
                        "系統提示",
                        javax.swing.JOptionPane.INFORMATION_MESSAGE
                    );
                    
                    // 成功創建後，通知大廳重新加載資料庫並更新畫面，然後顯示大廳
                    if (parentFrame != null) {
                        parentFrame.loadHeroesFromDatabase();
                        parentFrame.refreshUI();
                        parentFrame.setVisible(true);
                    }
                    
                    // 關閉當前視窗
                    AddHeroUI.this.dispose();
                    
                } catch (exception.ServiceException ex) {
                    // 將 Service 層拋出的業務異常（例如：名稱為空、重複名稱、點數防堵）顯示在介面上
                    lblErrorMessage.setText(ex.getMessage());
                }
            }
        });

        // 💡 預載：載入預設選中的男生圖片
        updateAvatarPreview();
        updatePointsUI();
    }

    /**
     * 💡 運行時所使用的建構子 (由 ChooseHeroUI 開啟時使用)
     * 
     * @param playerId 當前登入的玩家 ID
     * @param parentFrame 發起呼叫的 ChooseHeroUI 實體
     */
    public AddHeroUI(int playerId, ChooseHeroUI parentFrame) {
        this(); // 💡 關鍵：先呼叫上面的預設建構子，畫出畫面元件
        this.playerId = playerId;
        this.parentFrame = parentFrame;
    }

    /**
     * 💡 更新配點面板的標籤顯示 (以及控制按鈕的可點擊狀態)
     */
    private void updatePointsUI() {
        // 1. 更新剩餘點數標籤
        lblPointsVal.setText(String.valueOf(remainingPoints));
        
        // 2. 計算並更新各屬性的實際數值
        lblHpVal.setText(String.valueOf(BASE_HP + hpPoints * HP_PER_POINT));
        lblAtkVal.setText(String.valueOf(BASE_ATK + atkPoints));
        lblDefVal.setText(String.valueOf(BASE_DEF + defPoints));
        lblSpeedVal.setText(String.valueOf(BASE_SPEED + speedPoints));
        
        // 3. 防呆按鈕狀態調整
        // 如果沒有點數了，就把所有 "+" 按鈕停用
        boolean hasPoints = remainingPoints > 0;
        btnHpPlus.setEnabled(hasPoints);
        btnAtkPlus.setEnabled(hasPoints);
        btnDefPlus.setEnabled(hasPoints);
        btnSpeedPlus.setEnabled(hasPoints);
        
        // 如果該屬性分配的點數為 0，就停用對應的 "-" 按鈕 (防呆：不能減低於基礎值)
        btnHpMinus.setEnabled(hpPoints > 0);
        btnAtkMinus.setEnabled(atkPoints > 0);
        btnDefMinus.setEnabled(defPoints > 0);
        btnSpeedMinus.setEnabled(speedPoints > 0);
    }

    /**
     * 💡 根據目前單選按鈕選中的性別，動態載入對應的頭像圖片
     */
    private void updateAvatarPreview() {
        String imgName = radioMale.isSelected() ? "male_hero.jpg" : "female_hero.jpg";
        URL imgUrl = getClass().getResource("/images/" + imgName);
        
        if (imgUrl != null) {
            ImageIcon rawIcon = new ImageIcon(imgUrl);
            Image rawImg = rawIcon.getImage();
            
            // 縮放圖片以完全吻合 Label 的寬高
            Image scaledImg = rawImg.getScaledInstance(
                lblImagePreview.getWidth(), 
                lblImagePreview.getHeight(), 
                Image.SCALE_SMOOTH
            );
            lblImagePreview.setIcon(new ImageIcon(scaledImg));
            lblImagePreview.setText("");
        } else {
            lblImagePreview.setIcon(null);
            lblImagePreview.setText("圖片遺失 (" + imgName + ")");
        }
    }

    /**
     * 💡 安全返回大廳的方法
     */
    private void returnToLobby() {
        // 關閉目前視窗
        this.dispose();
        
        if (parentFrame != null) {
            // 顯示原大廳
            parentFrame.setVisible(true);
        } else {
            // 防呆：如果沒有父視窗引用，則重新建立一個大廳視窗
            ChooseHeroUI lobby = new ChooseHeroUI(playerId);
            lobby.setVisible(true);
        }
    }
}
