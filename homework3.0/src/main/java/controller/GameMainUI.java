package controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import controller.helper.UiTheme;

import vo.HeroVO;
import service.BattleService;
import dto.BattleTickResult;
import service.impl.BattleServiceImpl;

/**
 * 遊戲主視窗掛機大廳 (GameMainUI)
 * 💡 這是您親自設計的佈局，並經過 OOP/MVC 架構重構（關注點分離）：
 *   - UI (View) 層：只負責版面元件繪製、事件監聽與刷新畫面。
 *   - 戰鬥服務 (Service) 層：負責處理所有攻擊判定、點數累積與資料庫存取。
 *   - 兩層之間透過 DTO (BattleTickResult) 進行資料傳遞，極度乾淨！
 */
public class GameMainUI extends JFrame {

    private JPanel contentPane;
    
    // --- 玩家資訊元件 (左上) ---
    private JLabel lblPlayerAvatar;
    private JLabel lblPlayerName;
    private JProgressBar barPlayerExp;   // 經驗值百分比條
    private JProgressBar barPlayerHp;    // 生命值百分比條
    private JProgressBar barPlayerAction;// 玩家行動值條
    private JLabel lblAtkVal, lblDefVal, lblSpeedVal, lblHpVal;

    // --- 玩家裝備元件 (中上) ---
    private JPanel panelEquipHelmet;
    private JPanel panelEquipArmor;
    private JPanel panelEquipWeapon;
    private JPanel panelEquipBoots;
    
    // --- 💡 裝備插槽的文字標籤 (用於動態刷新裝備名字) ---
    private JLabel lblHelmetSlot;
    private JLabel lblArmorSlot;
    private JLabel lblWeaponSlot;
    private JLabel lblBootsSlot;

    // --- 怪物資訊元件 (右上) ---
    private JLabel lblMonsterName;
    private JProgressBar barMonsterHp;    // 怪物生命條
    private JProgressBar barMonsterAction;// 怪物行動值條

    // --- 戰鬥日誌與控制元件 (中下) ---
    private JComboBox<String> comboMapSelector;
    private JTextArea txtBattleLog;       // 戰鬥日誌文字區
    private JScrollPane scrollBattleLog;  // 滾動面板
    private JButton btnStartBattle;       // 開始戰鬥按鈕
    private JButton btnStopBattle;        // 停止戰鬥按鈕

    // --- 下方功能按鈕 (最下) ---
    private JButton btnBag;
    private JButton btnStatPoints;
    private JButton btnReturnLobby;
    private JButton btnReturnLogin;

    // --- 運行時數據狀態 ---
    private int playerId = 1;
    private HeroVO currentHero;

    // --- 💡 戰鬥與血量狀態變數 (已加入，修復編譯遺漏) ---
    private boolean isFighting = false;  // 是否正在掛機戰鬥中
    private int currentPlayerHp;         // 玩家當前血量 (用於戰鬥外休整)
    private int maxPlayerHp;             // 玩家最大血量上限 (含裝備加成)

    // 💡 關鍵重構：引入戰鬥業務邏輯協調服務 (Orchestration Service)
    private BattleService battleService = new BattleServiceImpl();
    private javax.swing.Timer battleTimer; // 負責定期發出戰鬥心跳的定時器

    /**
     * 💡 預設建構子 (供 Eclipse WindowBuilder 預覽版面使用)
     */
    public GameMainUI() {
        // 設定視窗標題與尺寸：寬 660, 高 520
        setTitle("RPG 放置遊戲 - 冒險掛機大廳");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 660, 520);
        setLocationRelativeTo(null); // 置中顯示
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // =========================================================================
        // 1. 左上：玩家資訊區 (Panel - Width: 195, Height: 220)
        // =========================================================================
        JPanel panelPlayerInfo = new JPanel();
        panelPlayerInfo.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "玩家資訊", 
            TitledBorder.LEADING, TitledBorder.TOP, new Font("微軟正黑體", Font.BOLD, 12)
        ));
        panelPlayerInfo.setBounds(15, 10, 195, 220);
        contentPane.add(panelPlayerInfo);
        panelPlayerInfo.setLayout(null);

        // 頭像 (縮放比例)
        lblPlayerAvatar = new JLabel("頭像預覽");
        lblPlayerAvatar.setHorizontalAlignment(SwingConstants.CENTER);
        lblPlayerAvatar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        lblPlayerAvatar.setBounds(10, 20, 60, 75);
        panelPlayerInfo.add(lblPlayerAvatar);

        // 名字與等級
        lblPlayerName = new JLabel("角色名字 Lv.1");
        lblPlayerName.setFont(new Font("微軟正黑體", Font.BOLD, 12));
        lblPlayerName.setBounds(80, 20, 105, 20);
        panelPlayerInfo.add(lblPlayerName);

        // 經驗條 (EXP)
        JLabel lblExp = new JLabel("EXP:");
        lblExp.setFont(new Font("微軟正黑體", Font.PLAIN, 10));
        lblExp.setBounds(80, 45, 30, 15);
        panelPlayerInfo.add(lblExp);

        barPlayerExp = new JProgressBar(0, 100);
        barPlayerExp.setValue(0);
        barPlayerExp.setStringPainted(true); // 顯示百分比文字 %
        barPlayerExp.setFont(new Font("微軟正黑體", Font.PLAIN, 9));
        barPlayerExp.setBounds(110, 45, 75, 15);
        panelPlayerInfo.add(barPlayerExp);

        // 生命條 (HP)
        JLabel lblHp = new JLabel("HP:");
        lblHp.setFont(new Font("微軟正黑體", Font.PLAIN, 10));
        lblHp.setBounds(80, 65, 30, 15);
        panelPlayerInfo.add(lblHp);

        barPlayerHp = new JProgressBar(0, 100);
        barPlayerHp.setValue(100);
        barPlayerHp.setStringPainted(true);
        barPlayerHp.setForeground(new Color(220, 53, 69)); // 紅色血條
        barPlayerHp.setFont(new Font("微軟正黑體", Font.PLAIN, 9));
        barPlayerHp.setBounds(110, 65, 75, 15);
        panelPlayerInfo.add(barPlayerHp);

        // 四大屬性與爆擊率 (垂直排列)
        JLabel lblHpTitle = new JLabel("生命值：");
        lblHpTitle.setFont(new Font("微軟正黑體", Font.PLAIN, 11));
        lblHpTitle.setBounds(10, 100, 50, 18);
        panelPlayerInfo.add(lblHpTitle);

        lblHpVal = new JLabel("100");
        lblHpVal.setFont(new Font("微軟正黑體", Font.BOLD, 11));
        lblHpVal.setBounds(60, 100, 40, 18);
        panelPlayerInfo.add(lblHpVal);

        JLabel lblAtkTitle = new JLabel("攻擊力：");
        lblAtkTitle.setFont(new Font("微軟正黑體", Font.PLAIN, 11));
        lblAtkTitle.setBounds(105, 100, 50, 18);
        panelPlayerInfo.add(lblAtkTitle);

        lblAtkVal = new JLabel("5");
        lblAtkVal.setFont(new Font("微軟正黑體", Font.BOLD, 11));
        lblAtkVal.setBounds(155, 100, 30, 18);
        panelPlayerInfo.add(lblAtkVal);

        JLabel lblDefTitle = new JLabel("防禦力：");
        lblDefTitle.setFont(new Font("微軟正黑體", Font.PLAIN, 11));
        lblDefTitle.setBounds(10, 122, 50, 18);
        panelPlayerInfo.add(lblDefTitle);

        lblDefVal = new JLabel("5");
        lblDefVal.setFont(new Font("微軟正黑體", Font.BOLD, 11));
        lblDefVal.setBounds(60, 122, 40, 18);
        panelPlayerInfo.add(lblDefVal);

        JLabel lblSpeedTitle = new JLabel("攻速：");
        lblSpeedTitle.setFont(new Font("微軟正黑體", Font.PLAIN, 11));
        lblSpeedTitle.setBounds(105, 122, 50, 18);
        panelPlayerInfo.add(lblSpeedTitle);

        lblSpeedVal = new JLabel("5");
        lblSpeedVal.setFont(new Font("微軟正黑體", Font.BOLD, 11));
        lblSpeedVal.setBounds(155, 122, 30, 18);
        panelPlayerInfo.add(lblSpeedVal);



        // 行動條 (Action)
        JLabel lblAction = new JLabel("行動值：");
        lblAction.setFont(new Font("微軟正黑體", Font.PLAIN, 11));
        lblAction.setBounds(10, 175, 50, 20);
        panelPlayerInfo.add(lblAction);

        barPlayerAction = new JProgressBar(0, 100);
        barPlayerAction.setValue(0);
        barPlayerAction.setStringPainted(true);
        barPlayerAction.setForeground(new Color(255, 193, 7)); // 黃色行動條
        barPlayerAction.setFont(new Font("微軟正黑體", Font.PLAIN, 9));
        barPlayerAction.setBounds(60, 175, 125, 20);
        panelPlayerInfo.add(barPlayerAction);

        // =========================================================================
        // 2. 中上：玩家裝備區 (Panel - Width: 195, Height: 220)
        // =========================================================================
        JPanel panelPlayerEquip = new JPanel();
        panelPlayerEquip.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "玩家裝備",
            TitledBorder.LEADING, TitledBorder.TOP, new Font("微軟正黑體", Font.BOLD, 12)
        ));
        panelPlayerEquip.setBounds(225, 10, 195, 220);
        contentPane.add(panelPlayerEquip);
        panelPlayerEquip.setLayout(null);

        // 裝備格 1: 頭盔
        panelEquipHelmet = new JPanel();
        panelEquipHelmet.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panelEquipHelmet.setBounds(20, 25, 65, 65);
        panelPlayerEquip.add(panelEquipHelmet);
        panelEquipHelmet.setLayout(null);
        lblHelmetSlot = new JLabel("頭部");
        lblHelmetSlot.setHorizontalAlignment(SwingConstants.CENTER);
        lblHelmetSlot.setFont(new Font("微軟正黑體", Font.PLAIN, 10));
        lblHelmetSlot.setBounds(0, 0, 65, 65);
        panelEquipHelmet.add(lblHelmetSlot);

        // 裝備格 2: 盔甲
        panelEquipArmor = new JPanel();
        panelEquipArmor.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panelEquipArmor.setBounds(110, 25, 65, 65);
        panelPlayerEquip.add(panelEquipArmor);
        panelEquipArmor.setLayout(null);
        lblArmorSlot = new JLabel("身體");
        lblArmorSlot.setHorizontalAlignment(SwingConstants.CENTER);
        lblArmorSlot.setFont(new Font("微軟正黑體", Font.PLAIN, 10));
        lblArmorSlot.setBounds(0, 0, 65, 65);
        panelEquipArmor.add(lblArmorSlot);

        // 裝備格 3: 武器
        panelEquipWeapon = new JPanel();
        panelEquipWeapon.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panelEquipWeapon.setBounds(20, 115, 65, 65);
        panelPlayerEquip.add(panelEquipWeapon);
        panelEquipWeapon.setLayout(null);
        lblWeaponSlot = new JLabel("武器");
        lblWeaponSlot.setHorizontalAlignment(SwingConstants.CENTER);
        lblWeaponSlot.setFont(new Font("微軟正黑體", Font.PLAIN, 10));
        lblWeaponSlot.setBounds(0, 0, 65, 65);
        panelEquipWeapon.add(lblWeaponSlot);

        // 裝備格 4: 鞋子
        panelEquipBoots = new JPanel();
        panelEquipBoots.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panelEquipBoots.setBounds(110, 115, 65, 65);
        panelPlayerEquip.add(panelEquipBoots);
        panelEquipBoots.setLayout(null);
        lblBootsSlot = new JLabel("鞋子");
        lblBootsSlot.setHorizontalAlignment(SwingConstants.CENTER);
        lblBootsSlot.setFont(new Font("微軟正黑體", Font.PLAIN, 10));
        lblBootsSlot.setBounds(0, 0, 65, 65);
        panelEquipBoots.add(lblBootsSlot);

        // =========================================================================
        // 3. 右上：怪物資訊區 (Panel - Width: 195, Height: 220)
        // =========================================================================
        JPanel panelMonsterInfo = new JPanel();
        panelMonsterInfo.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "怪物資訊",
            TitledBorder.LEADING, TitledBorder.TOP, new Font("微軟正黑體", Font.BOLD, 12)
        ));
        panelMonsterInfo.setBounds(435, 10, 195, 220);
        contentPane.add(panelMonsterInfo);
        panelMonsterInfo.setLayout(null);

        // 怪物名稱
        lblMonsterName = new JLabel("待機中...");
        lblMonsterName.setHorizontalAlignment(SwingConstants.CENTER);
        lblMonsterName.setFont(new Font("微軟正黑體", Font.BOLD, 16));
        lblMonsterName.setBounds(10, 35, 175, 30);
        panelMonsterInfo.add(lblMonsterName);

        // 怪物生命條 (HP)
        JLabel lblMonHp = new JLabel("生命值：");
        lblMonHp.setFont(new Font("微軟正黑體", Font.PLAIN, 11));
        lblMonHp.setBounds(15, 95, 50, 15);
        panelMonsterInfo.add(lblMonHp);

        barMonsterHp = new JProgressBar(0, 100);
        barMonsterHp.setValue(0);
        barMonsterHp.setStringPainted(true);
        barMonsterHp.setForeground(new Color(220, 53, 69)); // 紅色血條
        barMonsterHp.setFont(new Font("微軟正黑體", Font.PLAIN, 9));
        barMonsterHp.setBounds(70, 95, 110, 15);
        panelMonsterInfo.add(barMonsterHp);

        // 怪物行動值條 (Action)
        JLabel lblMonAction = new JLabel("行動值：");
        lblMonAction.setFont(new Font("微軟正黑體", Font.PLAIN, 11));
        lblMonAction.setBounds(15, 135, 50, 20);
        panelMonsterInfo.add(lblMonAction);

        barMonsterAction = new JProgressBar(0, 100);
        barMonsterAction.setValue(0);
        barMonsterAction.setStringPainted(true);
        barMonsterAction.setForeground(new Color(255, 193, 7)); // 黃色行動條
        barMonsterAction.setFont(new Font("微軟正黑體", Font.PLAIN, 9));
        barMonsterAction.setBounds(70, 135, 110, 20);
        panelMonsterInfo.add(barMonsterAction);

        // =========================================================================
        // 4. 中下：地圖選擇與戰鬥日誌 (Panel - Width: 615, Height: 165)
        // =========================================================================
        JPanel panelBattleArea = new JPanel();
        panelBattleArea.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "戰鬥冒險區",
            TitledBorder.LEADING, TitledBorder.TOP, new Font("微軟正黑體", Font.BOLD, 12)
        ));
        panelBattleArea.setBounds(15, 240, 615, 165);
        contentPane.add(panelBattleArea);
        panelBattleArea.setLayout(null);

        // 地圖選擇下拉選單 (JComboBox)
        JLabel lblMap = new JLabel("探索地圖：");
        lblMap.setFont(new Font("微軟正黑體", Font.BOLD, 12));
        lblMap.setBounds(15, 20, 70, 25);
        panelBattleArea.add(lblMap);

        comboMapSelector = new JComboBox<>();
        comboMapSelector.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        comboMapSelector.addItem("1. 新手草原 (Lv.1~5)");
        comboMapSelector.addItem("2. 幽暗森林 (Lv.6~10)");
        comboMapSelector.addItem("3. 烈焰火山 (Lv.11~15)");
        comboMapSelector.setBounds(85, 20, 180, 25);
        panelBattleArea.add(comboMapSelector);

        // 💡 戰鬥控制按鈕 (開始與停止戰鬥，設於右上角)
        btnStartBattle = new JButton("開始戰鬥");
        btnStartBattle.setFont(new Font("微軟正黑體", Font.BOLD, 11));
        btnStartBattle.setBounds(425, 20, 85, 25);
        panelBattleArea.add(btnStartBattle);

        btnStopBattle = new JButton("停止戰鬥");
        btnStopBattle.setFont(new Font("微軟正黑體", Font.BOLD, 11));
        btnStopBattle.setEnabled(false); // 預設為不可點擊狀態
        btnStopBattle.setBounds(515, 20, 85, 25);
        panelBattleArea.add(btnStopBattle);

        // 💡 戰鬥日誌滾動區域 (JTextArea + JScrollPane)
        txtBattleLog = new JTextArea();
        txtBattleLog.setEditable(false); // 唯讀，不讓使用者輸入文字
        txtBattleLog.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        txtBattleLog.append("=== 歡迎來到冒險大廳，請選擇地圖開始掛機 ===\n");

        scrollBattleLog = new JScrollPane(txtBattleLog);
        scrollBattleLog.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // 強制顯示垂直滾動條
        scrollBattleLog.setBounds(15, 55, 585, 95);
        panelBattleArea.add(scrollBattleLog);

        // =========================================================================
        // 5. 最下：選單按鈕區 (Panel - Width: 615, Height: 50)
        // =========================================================================
        JPanel panelMenu = new JPanel();
        panelMenu.setBounds(15, 415, 615, 50);
        contentPane.add(panelMenu);
        panelMenu.setLayout(null);

        btnBag = new JButton("背包");
        btnBag.setFont(new Font("微軟正黑體", Font.BOLD, 12));
        btnBag.setBounds(15, 5, 120, 35);
        panelMenu.add(btnBag);

        btnStatPoints = new JButton("屬性配點");
        btnStatPoints.setFont(new Font("微軟正黑體", Font.BOLD, 12));
        btnStatPoints.setBounds(165, 5, 120, 35);
        panelMenu.add(btnStatPoints);

        btnReturnLobby = new JButton("返回角色選擇");
        btnReturnLobby.setFont(new Font("微軟正黑體", Font.BOLD, 12));
        btnReturnLobby.setBounds(315, 5, 130, 35);
        panelMenu.add(btnReturnLobby);

        btnReturnLogin = new JButton("返回登入介面");
        btnReturnLogin.setFont(new Font("微軟正黑體", Font.BOLD, 12));
        btnReturnLogin.setBounds(470, 5, 130, 35);
        panelMenu.add(btnReturnLogin);
    }

    /**
     * 💡 運行時所使用的建構子 (由 ChooseHeroUI 大廳進入遊戲時呼叫)
     */
    public GameMainUI(int playerId, HeroVO hero) {
        this(); // 先載入上面的版面元件
        this.playerId = playerId;
        this.currentHero = hero;

        // 1. 💡 載入目前角色的數值資料到 UI 上 (結合裝備加成)
        lblPlayerName.setText(hero.getName() + " Lv." + hero.getLevel());
        refreshStats(); // 💡 重新計算「基礎屬性 + 裝備加成」後的真實戰力，避免白板數值進入遊戲

        // 顯示玩家的經驗值條 (每升一級需要 等級 * 100 經驗值)
        int expNeeded = hero.getLevel() * 100;
        barPlayerExp.setMaximum(expNeeded);
        barPlayerExp.setValue(hero.getExp());
        barPlayerExp.setString(hero.getExp() + " / " + expNeeded);



        // 載入頭像圖片
        URL imgUrl = getClass().getResource("/images/" + hero.getImgName());
        if (imgUrl != null) {
            ImageIcon rawIcon = new ImageIcon(imgUrl);
            java.awt.Image rawImg = rawIcon.getImage();
            java.awt.Image scaledImg = rawImg.getScaledInstance(
                lblPlayerAvatar.getWidth(), 
                lblPlayerAvatar.getHeight(), 
                java.awt.Image.SCALE_SMOOTH
            );
            lblPlayerAvatar.setIcon(new ImageIcon(scaledImg));
            lblPlayerAvatar.setText("");
        } else {
            lblPlayerAvatar.setIcon(null);
            lblPlayerAvatar.setText("無圖片");
        }

        // 2. 💡 初始化 Swing 計時器 (每 100 毫秒觸發一次戰鬥心跳 Tick)
        // 計時器只負責觸發 Tick，完全不包含任何戰鬥運算公式
        battleTimer = new javax.swing.Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 💡 呼叫服務層計算心跳
                BattleTickResult result = battleService.tick();
                
                // 💡 UI 只負責把計算好的數值繪製到介面上 (完全分工)
                barPlayerAction.setValue((int) result.getPlayerAction());
                barMonsterAction.setValue((int) result.getMonsterAction());
                barPlayerHp.setValue(result.getPlayerHp());
                barPlayerHp.setString(result.getPlayerHp() + " / " + result.getMaxPlayerHp());
                
                lblMonsterName.setText(result.getMonsterName());
                barMonsterHp.setMaximum(result.getMaxMonsterHp());
                barMonsterHp.setValue(result.getMonsterHp());
                barMonsterHp.setString(result.getMonsterHp() + " / " + result.getMaxMonsterHp());

                // 將本次 Tick 產生的文字印在日誌中
                for (String log : result.getLogMessages()) {
                    txtBattleLog.append(log);
                }
                
                if (!result.getLogMessages().isEmpty()) {
                    autoScrollLog();
                }

                // =========================================================================
                // 💡【GUI 學習筆記：戰鬥 UI 即時更新與效能優化 (Performance vs. UI Refresh)】
                // =========================================================================
                // 1. 為什麼不能在每次 Tick (每0.1秒) 都呼叫 `refreshStats()`？
                //    - `refreshStats()` 內部會向 MySQL 資料庫查詢角色最新的真實屬性 (含裝備加成)。
                //    - 如果每 100 毫秒都執行一次 SQL 查詢，會造成資料庫連線池瞬間被塞滿，導致嚴重的畫面卡頓。
                // 2. 什麼時候資料庫的「等級、EXP、基礎屬性」才會發生改變？
                //    - 只有在擊敗怪物的那一瞬間（發放獎勵與判定升級）。
                // 3. 解決方案 (Reactive UI Update)：
                //    - 我們平常 Tick 僅在記憶體中做極輕量的血量、行動值繪製。
                //    - 一旦後台服務回傳 `result.isMonsterDead() == true` (怪獸被擊殺)，
                //      才觸發一次 `refreshStats()` 從資料庫重新整理最權威的角色等級、經驗值條與基礎屬性。
                // =========================================================================
                if (result.isMonsterDead()) {
                    System.out.println("⚡ [DEBUG] UI 偵測到怪物死亡！即將刷新屬性與經驗值...");
                    refreshStats();
                } else {
                    lblPlayerName.setText(currentHero.getName() + " Lv." + currentHero.getLevel());
                    lblHpVal.setText(String.valueOf(result.getMaxPlayerHp()));
                }

                // 檢查玩家是否戰敗
                if (result.isPlayerDead()) {
                    // 戰敗自動將地圖跳回草原
                    comboMapSelector.setSelectedIndex(0);
                    
                    // 💡 停止計時器與重置戰鬥狀態，釋放背景資源
                    battleTimer.stop();
                    isFighting = false;
                    
                    // 恢復按鈕狀態
                    btnStartBattle.setEnabled(true);
                    btnStopBattle.setEnabled(false);
                    comboMapSelector.setEnabled(true);
                    
                    // 恢復玩家血量 (以加成後的生命值為基準)
                    currentPlayerHp = maxPlayerHp;
                    barPlayerHp.setValue(currentPlayerHp);
                    barPlayerHp.setString(currentPlayerHp + " / " + maxPlayerHp);
                    
                    // 清空怪物與行動條面板顯示
                    lblMonsterName.setText("待機中...");
                    barMonsterHp.setValue(0);
                    barMonsterHp.setString("0 / 0");
                    barMonsterAction.setValue(0);
                    barPlayerAction.setValue(0);
                }
            }
        });

        // 3. 💡 設定按鈕事件監聽器 (均呼叫服務層對接)
        
        // 開始戰鬥按鈕
        btnStartBattle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isFighting = true; // 💡 設為戰鬥中狀態
                
                // 停用按鈕與地圖選單，啟用停止按鈕
                btnStartBattle.setEnabled(false);
                btnStopBattle.setEnabled(true);
                comboMapSelector.setEnabled(false);
                
                int mapId = comboMapSelector.getSelectedIndex() + 1;
                txtBattleLog.append("\n🚩 隊伍進入了地圖 [" + comboMapSelector.getSelectedItem() + "]，開始掛機探險！\n");
                
                // 啟動後台戰鬥服務，並開啟計時器心跳
                battleService.startBattle(currentHero, mapId);
                battleTimer.start();
            }
        });

        // 停止戰鬥按鈕
        btnStopBattle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtBattleLog.append("🛑 停止掛機戰鬥，隊伍已休整。\n");
                autoScrollLog();
                
                // 呼叫服務層停止
                battleService.stopBattle();
                battleTimer.stop();
                isFighting = false; // 💡 設為停止狀態
                
                // 重置 UI 按鈕與進度條
                btnStartBattle.setEnabled(true);
                btnStopBattle.setEnabled(false);
                comboMapSelector.setEnabled(true);
                
                lblMonsterName.setText("待機中...");
                barMonsterHp.setValue(0);
                barMonsterHp.setString("0 / 0");
                barMonsterAction.setValue(0);
                barPlayerAction.setValue(0);
                
                // 恢復滿血狀態 (以加成後的生命值為基準)
                currentPlayerHp = maxPlayerHp;
                barPlayerHp.setValue(maxPlayerHp);
                barPlayerHp.setString(maxPlayerHp + " / " + maxPlayerHp);
            }
        });

        // 💡 背包按鈕點擊 (開啟對話框，傳入 parent 指標以便回呼)
        btnBag.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BagUI bagDialog = new BagUI(GameMainUI.this, currentHero.getId());
                bagDialog.setVisible(true);
            }
        });

        // 屬性配點按鈕點擊 (開啟 Modal 配點視窗)
        btnStatPoints.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StatPointsUI pointsDialog = new StatPointsUI(GameMainUI.this, currentHero.getId());
                pointsDialog.setVisible(true);
            }
        });

        // 返回大廳按鈕
        btnReturnLobby.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 安全防呆：必須先關閉後台計時器與戰鬥服務
                battleService.stopBattle();
                battleTimer.stop();
                
                GameMainUI.this.dispose();
                ChooseHeroUI lobby = new ChooseHeroUI(GameMainUI.this.playerId);
                lobby.setVisible(true);
            }
        });

        // 返回登入介面按鈕
        btnReturnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 安全防呆：必須先關閉後台計時器與戰鬥服務
                battleService.stopBattle();
                battleTimer.stop();
                
                GameMainUI.this.dispose();
                LoginFrame login = new LoginFrame();
                login.setVisible(true);
            }
        });
    }

    /**
     * 日誌文字區自動滾動至最底端
     */
    private void autoScrollLog() {
        txtBattleLog.setCaretPosition(txtBattleLog.getDocument().getLength());
    }

    /**
     * 💡 重新計算「基礎屬性 + 裝備加成」後的真實數值，並刷新 UI 面板標籤
     * 💡【學習筆記：跨子視窗即時連動刷新】
     *   - 當子對話框 (BagUI) 的裝備穿脫狀態改變時，會呼叫此方法。
     *   - 此方法直接將算好的屬性覆蓋到 currentHero 記憶體物件中。
     *   - 由於 BattleService 內部持有該 currentHero 的參考，戰鬥中的傷害與速度將會【即時與最新裝備連動】！
     */
    void refreshStats() {
        service.HeroStatsService statsService = new service.impl.HeroStatsServiceImpl();
        HeroVO combinedHero = statsService.getCombinedStats(currentHero.getId());

        if (combinedHero != null) {
            // 1. 同步更新記憶體物件，以供戰鬥計時器使用
            currentHero.setLevel(combinedHero.getLevel());
            currentHero.setExp(combinedHero.getExp());
            currentHero.setStatPoints(combinedHero.getStatPoints());
            currentHero.setHp(combinedHero.getHp());
            currentHero.setAtk(combinedHero.getAtk());
            currentHero.setDef(combinedHero.getDef());
            currentHero.setSpeed(combinedHero.getSpeed());

            // 2. 刷新左上角玩家面板上的文字標籤
            lblHpVal.setText(String.valueOf(combinedHero.getHp()));
            lblAtkVal.setText(String.valueOf(combinedHero.getAtk()));
            lblDefVal.setText(String.valueOf(combinedHero.getDef()));
            lblSpeedVal.setText(String.valueOf(combinedHero.getSpeed()));

            // =========================================================================
            // 💡【GUI 學習筆記：經驗值與等級 UI 進度條刷新】
            // =========================================================================
            // - 當角色升級或戰鬥擊殺怪物獲得經驗值時，我們必須同步更新：
            //   1. 經驗值條的最大上限 (等級 * 100)。
            //   2. 經驗值條的當前值。
            //   3. 經驗值條上顯示的文字字串 (如 "45 / 200")。
            //   4. 玩家名稱頭銜標籤 (如 "亞瑟 Lv.2")。
            // =========================================================================
            lblPlayerName.setText(combinedHero.getName() + " Lv." + combinedHero.getLevel());
            int expNeeded = combinedHero.getLevel() * 100;
            System.out.println("⚡ [DEBUG] refreshStats - 載入角色 EXP: " + combinedHero.getExp() + " / " + expNeeded);
            barPlayerExp.setMaximum(expNeeded);
            barPlayerExp.setValue(combinedHero.getExp());
            barPlayerExp.setString(combinedHero.getExp() + " / " + expNeeded);

            // 3. 更新血量條進度條最大值
            maxPlayerHp = combinedHero.getHp();
            barPlayerHp.setMaximum(maxPlayerHp);
            
            // 如果不在戰鬥中，直接將血量補滿 (安全區機制)
            if (!isFighting) {
                currentPlayerHp = maxPlayerHp;
                barPlayerHp.setValue(currentPlayerHp);
                barPlayerHp.setString(currentPlayerHp + " / " + maxPlayerHp);
            } else {
                // 如果在戰鬥中，血量不能歸滿，但如果目前血量大於新上限則進行截斷
                if (currentPlayerHp > maxPlayerHp) {
                    currentPlayerHp = maxPlayerHp;
                }
                barPlayerHp.setValue(currentPlayerHp);
                barPlayerHp.setString(currentPlayerHp + " / " + maxPlayerHp);
            }

            // 💡 4. 重新繪製中上裝備插槽的顯示文字 (顯示當前穿著裝備的名字與品級顏色)
            // 先重置回預設的 placeholder 字樣與灰色
            lblHelmetSlot.setText("頭部");
            lblHelmetSlot.setFont(new Font("微軟正黑體", Font.PLAIN, 10));
            lblHelmetSlot.setForeground(Color.GRAY);

            lblArmorSlot.setText("身體");
            lblArmorSlot.setFont(new Font("微軟正黑體", Font.PLAIN, 10));
            lblArmorSlot.setForeground(Color.GRAY);

            lblWeaponSlot.setText("武器");
            lblWeaponSlot.setFont(new Font("微軟正黑體", Font.PLAIN, 10));
            lblWeaponSlot.setForeground(Color.GRAY);

            lblBootsSlot.setText("鞋子");
            lblBootsSlot.setFont(new Font("微軟正黑體", Font.PLAIN, 10));
            lblBootsSlot.setForeground(Color.GRAY);

            // 從資料庫查出目前背後真正的裝備穿戴明細，將穿在身上的裝備名字畫在插槽上
            dao.HeroEquipmentDao bagDao = new dao.impl.HeroEquipmentDaoImpl();
            List<vo.HeroEquipmentVO> bagItems = bagDao.selectByHeroId(currentHero.getId());
            if (bagItems != null) {
                for (vo.HeroEquipmentVO item : bagItems) {
                    if (item.isEquipped()) {
                        String slot = item.getSlot().toLowerCase();
                        
                        // 💡 呼叫專屬 UI 配色工具類別，實現「單一控制點」的樣式管理
                        Color nameColor = UiTheme.getRarityColor(item.getTier());

                        // 填充文字並改為粗體
                        if ("helmet".equals(slot)) {
                            lblHelmetSlot.setText(item.getName());
                            lblHelmetSlot.setFont(new Font("微軟正黑體", Font.BOLD, 11));
                            lblHelmetSlot.setForeground(nameColor);
                        } else if ("armor".equals(slot)) {
                            lblArmorSlot.setText(item.getName());
                            lblArmorSlot.setFont(new Font("微軟正黑體", Font.BOLD, 11));
                            lblArmorSlot.setForeground(nameColor);
                        } else if ("weapon".equals(slot)) {
                            lblWeaponSlot.setText(item.getName());
                            lblWeaponSlot.setFont(new Font("微軟正黑體", Font.BOLD, 11));
                            lblWeaponSlot.setForeground(nameColor);
                        } else if ("boots".equals(slot)) {
                            lblBootsSlot.setText(item.getName());
                            lblBootsSlot.setFont(new Font("微軟正黑體", Font.BOLD, 11));
                            lblBootsSlot.setForeground(nameColor);
                        }
                    }
                }
            }
        }
    }
}
