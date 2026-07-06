package controller.helper;

import java.awt.Color;
import vo.HeroEquipmentVO;

/**
 * 遊戲 UI 視覺主題與顯示格式化工具類別 (UiTheme)
 * 
 * 💡【系統架構學習筆記：資料格式化與中文化元件抽離】
 *   - 為了讓 UI 視窗類別 (如 BagUI) 專注在視窗的佈局與按鈕事件處理上，
 *     我們將「資料翻譯」與「字串格式化」這類單純的【顯示輔助邏輯】統一抽取到本類別中。
 *   - 這樣做不僅能讓 BagUI.java 的行數大幅縮減，更能實現多介面共用（例如：未來如果要在主畫面顯示裝備詳細屬性提示，可直接呼叫 `UiTheme.getBonusString(...)`）。
 */
public class UiTheme {

    /**
     * 💡 根據裝備品級獲取原生 AWT Color 物件
     * 適用於：大廳插槽標籤文字顏色 (`lblHelmetSlot.setForeground(...)` 等)
     * 
     * @param tier 裝備品級 (1: 基礎, 2: 中級, 3: 高級)
     * @return Color AWT 顏色物件
     */
    public static Color getRarityColor(int tier) {
        switch (tier) {
            case 2:  
                return new Color(0, 123, 255); // 品級 2：精良 (藍色)
            case 3:  
                return new Color(128, 0, 128); // 品級 3：史詩 (紫色)
            default: 
                return new Color(40, 167, 69);  // 品級 1：基礎 (綠色)
        }
    }

    /**
     * 💡 根據裝備品級獲取 HTML 顏色代碼字串
     * 適用於：背包 JTable 儲存格的 HTML 渲染字串拼接 (`<font color='...'>`)
     * 
     * @param tier 裝備品級
     * @return String HTML 顏色代碼 (如 "green", "blue", "purple")
     */
    public static String getRarityHtmlColor(int tier) {
        switch (tier) {
            case 2:  
                return "blue";
            case 3:  
                return "purple";
            default: 
                return "green";
        }
    }

    /**
     * 💡 將資料庫部位英文代號翻譯為中文顯示名稱
     * 
     * @param slot 英文部位代號 (helmet, armor, weapon, boots)
     * @return String 中文部位名稱
     */
    public static String translateSlot(String slot) {
        if (slot == null) return "未知";
        switch (slot.toLowerCase()) {
            case "helmet": return "頭部";
            case "armor":  return "身體";
            case "weapon": return "武器";
            case "boots":  return "鞋子";
            default:       return "未知";
        }
    }

    /**
     * 💡 將裝備的複數屬性加成，合併成漂亮的單行格式化字串
     * 
     * @param vo 角色裝備屬性視圖物件
     * @return String 格式化後的加成字串 (例如 "攻擊 +5 生命 +20")
     */
    public static String getBonusString(HeroEquipmentVO vo) {
        if (vo == null) return "無加成";
        
        StringBuilder sb = new StringBuilder();
        if (vo.getHpBonus() > 0)    sb.append("生命 +").append(vo.getHpBonus()).append(" ");
        if (vo.getAtkBonus() > 0)   sb.append("攻擊 +").append(vo.getAtkBonus()).append(" ");
        if (vo.getDefBonus() > 0)   sb.append("防禦 +").append(vo.getDefBonus()).append(" ");
        if (vo.getSpeedBonus() > 0) sb.append("速度 +").append(vo.getSpeedBonus()).append(" ");
        
        String res = sb.toString().trim();
        return res.isEmpty() ? "無加成" : res;
    }
}
