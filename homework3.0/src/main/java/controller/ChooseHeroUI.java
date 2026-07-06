package controller;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.Font;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.List;

import vo.HeroVO;
import exception.ServiceException;
import service.HeroService;
import service.impl.HeroServiceImpl;

/**
 * 選擇角色大廳視窗 (ChooseHeroUI)
 * 💡 已與資料庫完成對接！支援跨視窗資料接收與真實資料庫查詢/刪除。
 */
public class ChooseHeroUI extends JFrame {

    private JPanel contentPane;
    
    // 右側屬性元件
    private JLabel lblHeroNameVal;
    private JLabel lblGenderVal;      // 性別
    private JLabel lblLevelVal;       // 等級 與 經驗值
    private JLabel lblAtkVal;         // 攻擊力
    private JLabel lblDefVal;         // 防禦力
    private JLabel lblSpeedVal;       // 速度
    private JLabel lblHpVal;          // 生命值 (替代爆擊率)
    private JLabel lblImageContainer; // 裝載角色圖片的 Label

    // 左側角色清單按鈕 (最多 5 個)
    private JButton[] btnHeroSlots = new JButton[5];
    private JButton btnDelete;        // 刪除按鈕

    // 儲存目前登入的玩家 ID
    private int playerId;

    // 引入角色業務邏輯 Service 物件，用於讀取與刪除角色資料
    private HeroService heroService = new HeroServiceImpl();

    // 儲存資料庫對接物件 `HeroVO` 的陣列
    private HeroVO[] myHeroes = new HeroVO[5]; 
    private int selectedSlot = 0; // 目前選中的槽位

    /**
     * 啟動程式進入點
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ChooseHeroUI frame = new ChooseHeroUI();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 建立選擇角色視窗 (預設建構子)
     * 💡【WindowBuilder 關鍵機制與錯誤修正】
     * 1. 錯誤原因：Eclipse WindowBuilder 視覺化設計工具在解析原始碼時，【只會解析預設建構子 (無參數建構子) 內部的元件】。
     *    如果我們把 GUI 初始化寫在有參數的建構子，並讓預設建構子使用 this(1) 委派呼叫，WindowBuilder 將會無法識別元件樹，導致 Design 畫面變空白或報錯。
     * 2. 解決方案：我們必須將所有的 GUI 元件宣告與版面佈局程式碼【全部搬回預設建構子內】。
     *    這樣 WindowBuilder 就能 100% 正常載入視覺化畫面！
     */
    public ChooseHeroUI() {
        this.playerId = 1; // 預設測試 ID (對應資料庫中的 testuser)
        
        setTitle("RPG 放置遊戲 - 選擇角色");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 420); // 寬度拉寬以容納左邊面板
        setLocationRelativeTo(null);   // 視窗居中顯示
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // ==========================================
        // 左側：角色選單面板 (JPanel)
        // ==========================================
        JPanel leftPanel = new JPanel();
        leftPanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
        leftPanel.setBounds(20, 20, 130, 330);
        contentPane.add(leftPanel);
        leftPanel.setLayout(null);

        // 標題
        JLabel lblListTitle = new JLabel("角色列表");
        lblListTitle.setFont(new Font("微軟正黑體", Font.BOLD, 14));
        lblListTitle.setBounds(35, 10, 60, 20);
        leftPanel.add(lblListTitle);

        // 建立 5 個槽位按鈕
        int startY = 45;
        int gapY = 40;
        for (int i = 0; i < 5; i++) {
            final int slotIndex = i;
            btnHeroSlots[i] = new JButton("未創建");
            btnHeroSlots[i].setFont(new Font("微軟正黑體", Font.PLAIN, 12));
            btnHeroSlots[i].setBounds(10, startY + (i * gapY), 110, 30);
            leftPanel.add(btnHeroSlots[i]);

            // 滑鼠點擊切換選中角色
            btnHeroSlots[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (myHeroes[slotIndex] != null) {
                        selectedSlot = slotIndex;
                        updateCharacterDisplay(); // 更新右側屬性與圖片
                    }
                }
            });
        }

        // 刪除角色按鈕
        btnDelete = new JButton("刪除角色");
        btnDelete.setFont(new Font("微軟正黑體", Font.BOLD, 12));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setBackground(new Color(220, 53, 69)); // 紅色背景
        btnDelete.setBounds(10, 285, 110, 30);
        leftPanel.add(btnDelete);

        // 刪除按鈕點擊事件 (串接 Service 層進行資料庫刪除)
        btnDelete.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (selectedSlot != -1 && myHeroes[selectedSlot] != null) {
                    HeroVO heroToDelete = myHeroes[selectedSlot];
                    
                    int confirm = javax.swing.JOptionPane.showConfirmDialog(
                        ChooseHeroUI.this,
                        "確定要永久刪除角色「" + heroToDelete.getName() + "」嗎？\n(注意：此動作將刪除該角色所有資料，無法復原！)",
                        "刪除確認",
                        javax.swing.JOptionPane.YES_NO_OPTION
                    );
                    
                    if (confirm == javax.swing.JOptionPane.YES_OPTION) {
                        try {
                            heroService.deletePlayerHero(heroToDelete.getId());
                            
                            javax.swing.JOptionPane.showMessageDialog(
                                ChooseHeroUI.this,
                                "角色「" + heroToDelete.getName() + "」已成功刪除！",
                                "系統提示",
                                javax.swing.JOptionPane.INFORMATION_MESSAGE
                            );
                            
                            loadHeroesFromDatabase();
                            
                            selectedSlot = -1;
                            for (int i = 0; i < 5; i++) {
                                if (myHeroes[i] != null) {
                                    selectedSlot = i;
                                    break;
                                }
                            }
                            
                            refreshUI();
                            
                        } catch (ServiceException ex) {
                            javax.swing.JOptionPane.showMessageDialog(
                                ChooseHeroUI.this,
                                ex.getMessage(),
                                "刪除失敗",
                                javax.swing.JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                }
            }
        });

        // ==========================================
        // 中間：角色圖片顯示區
        // ==========================================
        JPanel imgPanel = new JPanel();
        imgPanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
        imgPanel.setBounds(170, 20, 200, 260);
        contentPane.add(imgPanel);
        imgPanel.setLayout(null);

        // 圖片 Container (JLabel)
        lblImageContainer = new JLabel("");
        lblImageContainer.setBounds(10, 10, 180, 240);
        imgPanel.add(lblImageContainer);

        // ==========================================
        // 右側：屬性顯示區 (JLabel)
        // ==========================================
        JLabel lblNameTag = new JLabel("角色暱稱：");
        lblNameTag.setFont(new Font("微軟正黑體", Font.BOLD, 14));
        lblNameTag.setBounds(390, 25, 80, 20);
        contentPane.add(lblNameTag);

        lblHeroNameVal = new JLabel("無名");
        lblHeroNameVal.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        lblHeroNameVal.setBounds(470, 25, 100, 20);
        contentPane.add(lblHeroNameVal);

        JLabel lblAttrs = new JLabel("角色屬性：");
        lblAttrs.setFont(new Font("微軟正黑體", Font.BOLD, 14));
        lblAttrs.setBounds(390, 60, 80, 20);
        contentPane.add(lblAttrs);

        // 屬性 1 (性別)
        JLabel lblGender = new JLabel("1. 性別：");
        lblGender.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        lblGender.setBounds(400, 90, 80, 20);
        contentPane.add(lblGender);

        lblGenderVal = new JLabel("-");
        lblGenderVal.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        lblGenderVal.setBounds(480, 90, 80, 20);
        contentPane.add(lblGenderVal);

        // 屬性 2 (等級與經驗值合併顯示)
        JLabel lblLevel = new JLabel("2. 等級：");
        lblLevel.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        lblLevel.setBounds(400, 120, 80, 20);
        contentPane.add(lblLevel);

        lblLevelVal = new JLabel("-");
        lblLevelVal.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        lblLevelVal.setBounds(480, 120, 100, 20);
        contentPane.add(lblLevelVal);

        // 屬性 3 (戰鬥數值標題)
        JLabel lblCombatAttrs = new JLabel("3. 戰鬥數值：");
        lblCombatAttrs.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        lblCombatAttrs.setBounds(400, 150, 100, 20);
        contentPane.add(lblCombatAttrs);

        // 生命值
        JLabel lblHp = new JLabel("• 生命值：");
        lblHp.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        lblHp.setBounds(415, 175, 70, 20);
        contentPane.add(lblHp);

        lblHpVal = new JLabel("-");
        lblHpVal.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        lblHpVal.setBounds(485, 175, 70, 20);
        contentPane.add(lblHpVal);

        // 攻擊力
        JLabel lblAtk = new JLabel("• 攻擊力：");
        lblAtk.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        lblAtk.setBounds(415, 200, 70, 20);
        contentPane.add(lblAtk);

        lblAtkVal = new JLabel("-");
        lblAtkVal.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        lblAtkVal.setBounds(485, 200, 70, 20);
        contentPane.add(lblAtkVal);

        // 防禦力
        JLabel lblDef = new JLabel("• 防禦力：");
        lblDef.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        lblDef.setBounds(415, 225, 70, 20);
        contentPane.add(lblDef);

        lblDefVal = new JLabel("-");
        lblDefVal.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        lblDefVal.setBounds(485, 225, 70, 20);
        contentPane.add(lblDefVal);

        // 速度
        JLabel lblSpeed = new JLabel("• 速度：");
        lblSpeed.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        lblSpeed.setBounds(415, 250, 70, 20);
        contentPane.add(lblSpeed);

        lblSpeedVal = new JLabel("-");
        lblSpeedVal.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        lblSpeedVal.setBounds(485, 250, 70, 20);
        contentPane.add(lblSpeedVal);

        // ==========================================
        // 下方：按鈕動作區
        // ==========================================
        JButton btnEnterGame = new JButton("進入遊戲");
        btnEnterGame.setFont(new Font("微軟正黑體", Font.BOLD, 14));
        btnEnterGame.setBounds(170, 300, 95, 35);
        contentPane.add(btnEnterGame);

        JButton btnCreateHero = new JButton("創建角色");
        btnCreateHero.setFont(new Font("微軟正黑體", Font.BOLD, 14));
        btnCreateHero.setBounds(275, 300, 95, 35);
        contentPane.add(btnCreateHero);

        // 進入遊戲點擊
        btnEnterGame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (selectedSlot != -1 && myHeroes[selectedSlot] != null) {
                    // 1. 關閉目前大廳視窗
                    ChooseHeroUI.this.dispose();
                    
                    // 2. 建立並開啟遊戲主畫面，帶入當前玩家與選定角色的屬性資料
                    GameMainUI gameMain = new GameMainUI(playerId, myHeroes[selectedSlot]);
                    gameMain.setVisible(true);
                } else {
                    javax.swing.JOptionPane.showMessageDialog(
                        ChooseHeroUI.this,
                        "請先選擇或創建您的角色！",
                        "系統警告",
                        javax.swing.JOptionPane.WARNING_MESSAGE
                    );
                }
            }
        });

        // 創建角色點擊
        btnCreateHero.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 隱藏目前大廳視窗
                ChooseHeroUI.this.setVisible(false);
                // 建立並顯示新增角色視窗，傳入當前玩家 ID 與大廳視窗 this
                AddHeroUI addHeroUi = new AddHeroUI(playerId, ChooseHeroUI.this);
                addHeroUi.setVisible(true);
            }
        });

        // 💡 預載加載 (以便 WindowBuilder 能讀取預設測試資料進行預覽)
        loadHeroesFromDatabase();
        refreshUI();
    }

    /**
     * 💡【真實登入跳轉使用的建構子】
     * 1. 運作原理：先使用 this() 呼叫上面的無參數建構子，把所有的視窗版面與元件建立好。
     * 2. 重新設定真實的 playerId (覆蓋掉預設測試值 1)。
     * 3. 再次呼叫資料庫，撈取該玩家在 MySQL 裡的真實角色。
     * 4. 重新刷洗畫面。
     * 
     * 💡【為什麼這樣設計？】
     * 如此一來，既能讓 WindowBuilder 在 Design 頁面完美讀取解析（因為有預設建構子），
     * 又能讓 LoginFrame 在執行時成功把玩家 ID 傳進來，兩全其美！
     */
    public ChooseHeroUI(int playerId) {
        this(); // 💡 關鍵：先呼叫無參數建構子，建立畫面元件
        this.playerId = playerId; // 覆蓋為登入玩家的真實 ID
        loadHeroesFromDatabase(); // 從資料庫重新載入該玩家的專屬角色
        refreshUI(); // 刷新渲染畫面
    }

    /**
     * 💡 從資料庫加載真實角色列表 (改為 package-private 權限，讓 AddHeroUI 可以呼叫刷新)
     */
    void loadHeroesFromDatabase() {
        try {
            List<HeroVO> dbList = heroService.showPlayerHeroById(playerId);
            
            for (int i = 0; i < 5; i++) {
                myHeroes[i] = null;
            }
            
            for (int i = 0; i < dbList.size() && i < 5; i++) {
                myHeroes[i] = dbList.get(i);
            }
        } catch (ServiceException e) {
            javax.swing.JOptionPane.showMessageDialog(
                this, 
                e.getMessage(), 
                "資料載入失敗", 
                javax.swing.JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * 重新整理整個 UI 畫面 (改為 package-private 權限，讓 AddHeroUI 可以呼叫刷新)
     */
    void refreshUI() {
        for (int i = 0; i < 5; i++) {
            if (myHeroes[i] != null) {
                btnHeroSlots[i].setText(myHeroes[i].getName());
                btnHeroSlots[i].setVisible(true);
            } else {
                btnHeroSlots[i].setVisible(false);
            }
        }
        updateCharacterDisplay();
    }

    /**
     * 更新右側的角色細節屬性與圖片
     */
    private void updateCharacterDisplay() {
        if (selectedSlot == -1 || myHeroes[selectedSlot] == null) {
            lblHeroNameVal.setText("無");
            lblGenderVal.setText("-");
            lblLevelVal.setText("-");
            lblHpVal.setText("-");
            lblAtkVal.setText("-");
            lblDefVal.setText("-");
            lblSpeedVal.setText("-");
            lblImageContainer.setIcon(null);
            lblImageContainer.setText("請點選左側角色");
            btnDelete.setEnabled(false);
            return;
        }

        btnDelete.setEnabled(true);
        HeroVO hero = myHeroes[selectedSlot];

        lblHeroNameVal.setText(hero.getName());
        lblGenderVal.setText(hero.getGender());
        lblLevelVal.setText("Lv. " + hero.getLevel());
        lblHpVal.setText(String.valueOf(hero.getHp()));
        lblAtkVal.setText(String.valueOf(hero.getAtk()));
        lblDefVal.setText(String.valueOf(hero.getDef()));
        lblSpeedVal.setText(String.valueOf(hero.getSpeed()));
        lblImageContainer.setText("");

        URL imgUrl = getClass().getResource("/images/" + hero.getImgName());
        if (imgUrl != null) {
            ImageIcon rawIcon = new ImageIcon(imgUrl);
            java.awt.Image rawImg = rawIcon.getImage();
            
            java.awt.Image scaledImg = rawImg.getScaledInstance(
                lblImageContainer.getWidth(), 
                lblImageContainer.getHeight(), 
                java.awt.Image.SCALE_SMOOTH
            );
            lblImageContainer.setIcon(new ImageIcon(scaledImg));
        } else {
            lblImageContainer.setIcon(null);
            lblImageContainer.setText("圖片未匯入 (" + hero.getImgName() + ")");
        }
    }
}
