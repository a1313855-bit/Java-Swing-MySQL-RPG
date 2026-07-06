package controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import vo.HeroVO;
import service.HeroStatsService;
import service.impl.HeroStatsServiceImpl;
import dao.HeroDao;
import dao.impl.HeroDaoImpl;

/**
 * 角色升級屬性配點對話框 (StatPointsUI)
 * 💡【軟體架構學習筆記：被動視圖與事務配點】
 *   - 本視窗採用 Modal JDialog 設計，確保玩家必須完成配點或取消才能繼續遊戲。
 *   - 採用「本地緩衝加點」機制：玩家點擊 + 和 - 只會變更暫時的分配點數，
 *     只有當玩家最終按下「確認保存」時，才會呼叫 Service 將點數一次寫入 MySQL 資料庫，節省資料庫開銷並防止手滑。
 */
public class StatPointsUI extends JDialog {

    private static final long serialVersionUID = 1L;

    private final JPanel contentPanel = new JPanel();
    private GameMainUI parent;
    private int heroId;

    // --- 外部 DAO 與業務邏輯服務 ---
    private HeroDao heroDao = new HeroDaoImpl();
    private HeroStatsService statsService = new HeroStatsServiceImpl();

    // --- 數據資料緩衝 (State) ---
    private HeroVO originalHero;      // 資料庫讀出的初始角色狀態
    private int remainingPoints = 0;  // 剩餘可分配點數
    private int hpAlloc = 0;          // 生命暫時分配點數
    private int atkAlloc = 0;         // 攻擊暫時分配點數
    private int defAlloc = 0;         // 防禦暫時分配點數
    private int speedAlloc = 0;       // 速度暫時分配點數

    // --- Swing 元件 ---
    private JLabel lblRemainingPoints;
    private JLabel lblHpVal;
    private JLabel lblAtkVal;
    private JLabel lblDefVal;
    private JLabel lblSpeedVal;

    private JButton btnHpMinus, btnHpPlus;
    private JButton btnAtkMinus, btnAtkPlus;
    private JButton btnDefMinus, btnDefPlus;
    private JButton btnSpeedMinus, btnSpeedPlus;

    private JButton btnConfirm;
    private JButton btnCancel;

    public StatPointsUI(GameMainUI parent, int heroId) {
        super(parent, "角色屬性配點", true); // 設定為 Modal 對話框
        this.parent = parent;
        this.heroId = heroId;

        // 💡 1. 初始化資料
        loadHeroData();

        // 💡 2. 初始化介面元件
        setResizable(false);
        setBounds(100, 100, 380, 320);
        setLocationRelativeTo(parent); // 置中於主畫面

        getContentPane().setLayout(null);
        contentPanel.setBounds(0, 0, 364, 281);
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPanel.setLayout(null);
        getContentPane().add(contentPanel);

        // 剩餘點數標籤
        lblRemainingPoints = new JLabel("剩餘可用屬性點： " + remainingPoints);
        lblRemainingPoints.setHorizontalAlignment(SwingConstants.CENTER);
        lblRemainingPoints.setFont(new Font("微軟正黑體", Font.BOLD, 15));
        lblRemainingPoints.setBounds(10, 20, 344, 25);
        contentPanel.add(lblRemainingPoints);

        // --- 生命值列 (Y = 60) ---
        JLabel lblHpTitle = new JLabel("生命上限 (HP)：");
        lblHpTitle.setFont(new Font("微軟正黑體", Font.BOLD, 13));
        lblHpTitle.setBounds(30, 60, 100, 25);
        contentPanel.add(lblHpTitle);

        lblHpVal = new JLabel();
        lblHpVal.setFont(new Font("Consolas", Font.PLAIN, 13));
        lblHpVal.setBounds(130, 60, 110, 25);
        contentPanel.add(lblHpVal);

        btnHpMinus = new JButton("-");
        btnHpMinus.setFont(new Font("Consolas", Font.BOLD, 11));
        btnHpMinus.setBounds(240, 60, 45, 25);
        contentPanel.add(btnHpMinus);

        btnHpPlus = new JButton("+");
        btnHpPlus.setFont(new Font("Consolas", Font.BOLD, 11));
        btnHpPlus.setBounds(295, 60, 45, 25);
        contentPanel.add(btnHpPlus);

        // --- 攻擊力列 (Y = 100) ---
        JLabel lblAtkTitle = new JLabel("攻擊強度 (ATK)：");
        lblAtkTitle.setFont(new Font("微軟正黑體", Font.BOLD, 13));
        lblAtkTitle.setBounds(30, 100, 100, 25);
        contentPanel.add(lblAtkTitle);

        lblAtkVal = new JLabel();
        lblAtkVal.setFont(new Font("Consolas", Font.PLAIN, 13));
        lblAtkVal.setBounds(130, 100, 110, 25);
        contentPanel.add(lblAtkVal);

        btnAtkMinus = new JButton("-");
        btnAtkMinus.setFont(new Font("Consolas", Font.BOLD, 11));
        btnAtkMinus.setBounds(240, 100, 45, 25);
        contentPanel.add(btnAtkMinus);

        btnAtkPlus = new JButton("+");
        btnAtkPlus.setFont(new Font("Consolas", Font.BOLD, 11));
        btnAtkPlus.setBounds(295, 100, 45, 25);
        contentPanel.add(btnAtkPlus);

        // --- 防禦力列 (Y = 140) ---
        JLabel lblDefTitle = new JLabel("防禦強度 (DEF)：");
        lblDefTitle.setFont(new Font("微軟正黑體", Font.BOLD, 13));
        lblDefTitle.setBounds(30, 140, 100, 25);
        contentPanel.add(lblDefTitle);

        lblDefVal = new JLabel();
        lblDefVal.setFont(new Font("Consolas", Font.PLAIN, 13));
        lblDefVal.setBounds(130, 140, 110, 25);
        contentPanel.add(lblDefVal);

        btnDefMinus = new JButton("-");
        btnDefMinus.setFont(new Font("Consolas", Font.BOLD, 11));
        btnDefMinus.setBounds(240, 140, 45, 25);
        contentPanel.add(btnDefMinus);

        btnDefPlus = new JButton("+");
        btnDefPlus.setFont(new Font("Consolas", Font.BOLD, 11));
        btnDefPlus.setBounds(295, 140, 45, 25);
        contentPanel.add(btnDefPlus);

        // --- 速度列 (Y = 180) ---
        JLabel lblSpeedTitle = new JLabel("攻速/速度(SPD)：");
        lblSpeedTitle.setFont(new Font("微軟正黑體", Font.BOLD, 13));
        lblSpeedTitle.setBounds(30, 180, 100, 25);
        contentPanel.add(lblSpeedTitle);

        lblSpeedVal = new JLabel();
        lblSpeedVal.setFont(new Font("Consolas", Font.PLAIN, 13));
        lblSpeedVal.setBounds(130, 180, 110, 25);
        contentPanel.add(lblSpeedVal);

        btnSpeedMinus = new JButton("-");
        btnSpeedMinus.setFont(new Font("Consolas", Font.BOLD, 11));
        btnSpeedMinus.setBounds(240, 180, 45, 25);
        contentPanel.add(btnSpeedMinus);

        btnSpeedPlus = new JButton("+");
        btnSpeedPlus.setFont(new Font("Consolas", Font.BOLD, 11));
        btnSpeedPlus.setBounds(295, 180, 45, 25);
        contentPanel.add(btnSpeedPlus);

        // --- 功能按鈕列 (Y = 230) ---
        btnConfirm = new JButton("確認保存");
        btnConfirm.setFont(new Font("微軟正黑體", Font.BOLD, 12));
        btnConfirm.setBounds(60, 230, 110, 35);
        contentPanel.add(btnConfirm);

        btnCancel = new JButton("取消");
        btnCancel.setFont(new Font("微軟正黑體", Font.BOLD, 12));
        btnCancel.setBounds(190, 230, 110, 35);
        contentPanel.add(btnCancel);

        // 💡 3. 綁定按鈕事件與邏輯
        bindEvents();

        // 💡 4. 初始化顯示
        refreshLabels();
    }

    /**
     * 從資料庫讀出最新的角色數據以進行配點
     */
    private void loadHeroData() {
        this.originalHero = heroDao.selectByHeroId(heroId);
        if (originalHero != null) {
            this.remainingPoints = originalHero.getStatPoints();
        } else {
            this.remainingPoints = 0;
        }
        // 重置分配緩衝區
        this.hpAlloc = 0;
        this.atkAlloc = 0;
        this.defAlloc = 0;
        this.speedAlloc = 0;
    }

    /**
     * 綁定介面按鈕的加點與扣點事件
     */
    private void bindEvents() {
        // --- HP 點數事件 ---
        btnHpPlus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (remainingPoints > 0) {
                    hpAlloc++;
                    remainingPoints--;
                    refreshLabels();
                }
            }
        });
        btnHpMinus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (hpAlloc > 0) {
                    hpAlloc--;
                    remainingPoints++;
                    refreshLabels();
                }
            }
        });

        // --- ATK 點數事件 ---
        btnAtkPlus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (remainingPoints > 0) {
                    atkAlloc++;
                    remainingPoints--;
                    refreshLabels();
                }
            }
        });
        btnAtkMinus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (atkAlloc > 0) {
                    atkAlloc--;
                    remainingPoints++;
                    refreshLabels();
                }
            }
        });

        // --- DEF 點數事件 ---
        btnDefPlus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (remainingPoints > 0) {
                    defAlloc++;
                    remainingPoints--;
                    refreshLabels();
                }
            }
        });
        btnDefMinus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (defAlloc > 0) {
                    defAlloc--;
                    remainingPoints++;
                    refreshLabels();
                }
            }
        });

        // --- SPEED 點數事件 ---
        btnSpeedPlus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (remainingPoints > 0) {
                    speedAlloc++;
                    remainingPoints--;
                    refreshLabels();
                }
            }
        });
        btnSpeedMinus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (speedAlloc > 0) {
                    speedAlloc--;
                    remainingPoints++;
                    refreshLabels();
                }
            }
        });

        // --- 確認按鈕事件 ---
        btnConfirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int totalAlloc = hpAlloc + atkAlloc + defAlloc + speedAlloc;
                if (totalAlloc == 0) {
                    JOptionPane.showMessageDialog(StatPointsUI.this, "您尚未分配任何屬性點！", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 呼叫 Service 執行配點保存
                boolean success = statsService.allocateStatPoints(heroId, hpAlloc, atkAlloc, defAlloc, speedAlloc);
                if (success) {
                    JOptionPane.showMessageDialog(StatPointsUI.this, "屬性配點保存成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                    parent.refreshStats(); // 💡 重算並重新刷新主畫面上的屬性面板與戰力
                    dispose();             // 關閉視窗
                } else {
                    JOptionPane.showMessageDialog(StatPointsUI.this, "儲存屬性配點失敗！", "錯誤", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // --- 取消按鈕事件 ---
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // 直接關閉不保存
            }
        });
    }

    /**
     * 更新畫面上顯示的數值 (原數值 + 暫時配點) 與按鈕可用狀態
     */
    private void refreshLabels() {
        lblRemainingPoints.setText("剩餘可用屬性點： " + remainingPoints);

        // 生命上限
        if (hpAlloc > 0) {
            lblHpVal.setText(originalHero.getHp() + " (+" + (hpAlloc * 15) + ")");
            lblHpVal.setForeground(new Color(40, 167, 69)); // 綠色
        } else {
            lblHpVal.setText(String.valueOf(originalHero.getHp()));
            lblHpVal.setForeground(Color.BLACK);
        }

        // 攻擊力
        if (atkAlloc > 0) {
            lblAtkVal.setText(originalHero.getAtk() + " (+" + atkAlloc + ")");
            lblAtkVal.setForeground(new Color(40, 167, 69));
        } else {
            lblAtkVal.setText(String.valueOf(originalHero.getAtk()));
            lblAtkVal.setForeground(Color.BLACK);
        }

        // 防禦力
        if (defAlloc > 0) {
            lblDefVal.setText(originalHero.getDef() + " (+" + defAlloc + ")");
            lblDefVal.setForeground(new Color(40, 167, 69));
        } else {
            lblDefVal.setText(String.valueOf(originalHero.getDef()));
            lblDefVal.setForeground(Color.BLACK);
        }

        // 速度
        if (speedAlloc > 0) {
            lblSpeedVal.setText(originalHero.getSpeed() + " (+" + speedAlloc + ")");
            lblSpeedVal.setForeground(new Color(40, 167, 69));
        } else {
            lblSpeedVal.setText(String.valueOf(originalHero.getSpeed()));
            lblSpeedVal.setForeground(Color.BLACK);
        }

        // --- 根據點數動態控制按鈕啟用狀態 (防呆) ---
        btnHpMinus.setEnabled(hpAlloc > 0);
        btnAtkMinus.setEnabled(atkAlloc > 0);
        btnDefMinus.setEnabled(defAlloc > 0);
        btnSpeedMinus.setEnabled(speedAlloc > 0);

        boolean hasPoints = remainingPoints > 0;
        btnHpPlus.setEnabled(hasPoints);
        btnAtkPlus.setEnabled(hasPoints);
        btnDefPlus.setEnabled(hasPoints);
        btnSpeedPlus.setEnabled(hasPoints);

        // 確認按鈕在有分配點數時才可用
        btnConfirm.setEnabled((hpAlloc + atkAlloc + defAlloc + speedAlloc) > 0);
    }
}
