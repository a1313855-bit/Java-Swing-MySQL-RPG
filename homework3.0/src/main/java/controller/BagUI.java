package controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import vo.HeroEquipmentVO;
import controller.helper.UiTheme;
import service.BagService;
import service.impl.BagServiceImpl;

/**
 * 角色背包彈出式視窗 (BagUI)
 * 
 * 💡【軟體架構與 GUI 學習筆記：JDialog (對話框)】
 *   1. 什麼是 JDialog？
 *      - JDialog 是 Java Swing 用來製作「彈出式子視窗」的元件。它與 JFrame (主視窗) 的主要差別在於：
 *        - JDialog 通常必須依附於一個父視窗 (在這裡是 GameMainUI)。
 *        - 當設定 `modal = true` (阻擋模式) 時，該對話框會「卡住」父視窗。玩家必須先關閉背包，才能繼續點擊主畫面的按鈕。
 *        - 這能有效防範玩家在開著背包的同時，跑去點擊主畫面的「開始戰鬥」導致數據衝突的 Bug！
 *   2. JTable 與 DefaultTableModel 的配合：
 *      - JTable 是表格元件，用來呈現二維表格。
 *      - DefaultTableModel 是表格的「資料模型 (Model)」，負責管理表格裡的列數與欄位內容。
 *      - 我們不讓 JTable 直接存取資料，而是透過操作 TableModel 來控制表格內容的更新與刷新。
 */
public class BagUI extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JTable table;
    private DefaultTableModel tableModel;
    
    // --- 業務邏輯服務層 ---
    private BagService bagService = new BagServiceImpl();
    
    // --- 狀態變數 ---
    private int heroId;
    private GameMainUI parentFrame; // 儲存父視窗引用，以便裝備改變時通知父視窗重算屬性
    private List<HeroEquipmentVO> bagItems; // 目前背包中的裝備視圖清單

    /**
     * 💡 建構子
     * @param parent 父視窗 (GameMainUI)
     * @param heroId 角色 ID
     */
    public BagUI(GameMainUI parent, int heroId) {
        // 💡 呼叫父類別建構子：傳入父 JFrame、視窗標題、並設定為 modal 阻擋模式 (true)
        super(parent, "角色背包", true);
        
        this.parentFrame = parent;
        this.heroId = heroId;
        
        // 設定視窗尺寸 (寬 450, 高 350)
        setBounds(100, 100, 450, 350);
        setLocationRelativeTo(parent); // 置中顯示於父視窗上方
        getContentPane().setLayout(new BorderLayout());
        
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);

        // =========================================================================
        // 1. 中間：裝備滾動表格 (JTable + JScrollPane)
        // =========================================================================
        
        // 宣告表格的標頭
        String[] columnNames = {"名稱", "部位", "加成屬性", "狀態"};
        
        // 初始化表格資料模型 (覆寫 isCellEditable 讓表格不可直接雙擊修改文字)
        tableModel = new DefaultTableModel(new Object[][]{}, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 唯讀
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 設定只能單選一行
        table.getTableHeader().setReorderingAllowed(false); // 停用拖曳欄位順序的功能
        
        // 套入滾動面板中
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(15, 15, 400, 220);
        contentPanel.add(scrollPane);

        // =========================================================================
        // 2. 下方：操作按鈕區
        // =========================================================================
        JButton btnToggleEquip = new JButton("穿戴 / 脫下");
        btnToggleEquip.setFont(new Font("微軟正黑體", Font.BOLD, 12));
        btnToggleEquip.setBounds(160, 255, 110, 35);
        contentPanel.add(btnToggleEquip);

        // 載入背包資料
        loadBagData();

        // =========================================================================
        // 💡 事件監聽器設定
        // =========================================================================
        
        // 穿戴/脫下按鈕點擊
        btnToggleEquip.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 1. 取得目前被選取的列索引
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(BagUI.this, "請先選擇背包中的一件裝備！", "系統提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 2. 從清單中找出對應的裝備
                HeroEquipmentVO selectedItem = bagItems.get(selectedRow);

                // 3. 呼叫服務層進行穿戴/脫下邏輯運算 (內部會自動處理部位替換)
                boolean success = bagService.toggleEquipStatus(selectedItem.getId(), BagUI.this.heroId);
                
                if (success) {
                    // 4. 💡 成功後重新整理背包表格資料
                    loadBagData();
                    
                    // 5. 💡 核心水管接通：通知父大廳視窗「重算裝備屬性加成」並重新繪製數值
                    parentFrame.refreshStats();
                } else {
                    JOptionPane.showMessageDialog(BagUI.this, "裝備狀態變更失敗！", "錯誤", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    /**
     * 💡 讀取資料庫背包清單並加載至 JTable 畫面上
     */
    private void loadBagData() {
        // 清空現有表格內容
        tableModel.setRowCount(0);

        // 呼叫 Service 讀取最新背包清單
        bagItems = bagService.getBagItems(heroId);

        if (bagItems == null || bagItems.isEmpty()) {
            return;
        }

        // =========================================================================
        // 💡【GUI 學習筆記：Swing HTML 渲染「黑魔法」的運作原理與用法】
        // =========================================================================
        // 1. 運作原理 (How it works)：
        //    - Swing 的文字元件 (如 JLabel、JButton) 內部其實內嵌了一個極簡版的 HTML 3.2 渲染引擎。
        //    - JTable 預設顯示儲存格時，是使用 `DefaultTableCellRenderer`，它繼承自 `JLabel`。
        //    - 當 Swing 偵測到傳入的字串是以小寫的 `<html>` 開頭時，它就會自動「切換模式」，
        //      不再以一般純文字繪製，而是啟動 HTML 解析器來解析裡面的標籤 (Tags)！
        // 2. 書寫語法 (Syntax Rules)：
        //    - 開頭必須為 `<html>`，結尾必須為 `</html>`。
        //    - 可以使用常用的 HTML 標籤，例如：
        //      - `<b>...</b>`：粗體字。
        //      - `<i>...</i>`：斜體字。
        //      - `<font color='顏色名稱或十六進制色碼'>...</font>`：設定文字顏色 (支援 "red", "green", "#800080" 等)。
        //      - `<br>`：強制換行。
        // 3. 業界使用時機與優缺點：
        //    - 【優點】極速開發！免去寫幾十行複雜的 `TableCellRenderer` 子類別，直接用簡單的字串拼接即可達成富文本 (Rich Text) 效果。
        //    - 【缺點】因為每次顯示都需要解析 HTML 語意，效能開銷比純文字高。
        //    - 【準則】適用於小型表格 (例如玩家背包幾十格)；但如果是上萬筆資料的巨量表格，不建議使用 HTML 渲染，否則滾動時會卡頓。
        // =========================================================================
        for (HeroEquipmentVO item : bagItems) {
            // 💡 呼叫專屬 UI 配色工具類別，取得 HTML 著色字串 (實踐 DRY 原則)
            String colorHex = UiTheme.getRarityHtmlColor(item.getTier());

            // 💡 拼接 HTML 字串：產生像是 "<html><font color='purple'><b>精鋼大劍</b></font></html>"
            String nameHtml = "<html><font color='" + colorHex + "'><b>" + item.getName() + "</b></font></html>";
            
            // 💡 穿戴狀態也透過 HTML 加上色彩：穿戴中顯示粗體綠色，未穿戴顯示普通灰色
            String statusHtml = item.isEquipped() 
                ? "<html><font color='green'><b>已穿戴</b></font></html>" 
                : "<html><font color='gray'>在背包中</font></html>";

            String slotChinese = UiTheme.translateSlot(item.getSlot());
            String bonusString = UiTheme.getBonusString(item);

            tableModel.addRow(new Object[]{
                nameHtml,
                slotChinese,
                bonusString,
                statusHtml
            });
        }
    }

}
