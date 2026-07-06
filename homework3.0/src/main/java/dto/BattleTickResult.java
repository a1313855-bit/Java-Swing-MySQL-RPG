package dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 戰鬥心跳結果傳輸物件 (BattleTickResult - DTO)
 * 💡【軟體架構學習筆記：資料傳輸物件 (Data Transfer Object)】
 *   - 本類別已移動到最外層的 `dto` 套件中，實現了與 `service` 和 `entity` 平行解耦的乾淨架構。
 *   - 它是一個純粹的資料打包容器，用來打包每一次戰鬥心跳產生的數值，將之一次傳回 UI，避免過多的參數傳遞。
 */
public class BattleTickResult {

    private double playerAction;      // 玩家累積行動值 (0~100)
    private double monsterAction;     // 怪物累積行動值 (0~100)
    private int playerHp;             // 玩家當前生命值
    private int maxPlayerHp;          // 玩家最大生命值
    private int monsterHp;            // 怪物當前生命值
    private int maxMonsterHp;         // 怪物最大生命值
    private String monsterName;       // 怪物名稱與等級
    private boolean isPlayerDead;     // 玩家是否陣亡
    private boolean isMonsterDead;    // 怪物是否死亡
    private List<String> logMessages = new ArrayList<>(); // 本次心跳產生的戰鬥訊息文字列表

    public BattleTickResult() {
    }

    public double getPlayerAction() {
        return playerAction;
    }

    public void setPlayerAction(double playerAction) {
        this.playerAction = playerAction;
    }

    public double getMonsterAction() {
        return monsterAction;
    }

    public void setMonsterAction(double monsterAction) {
        this.monsterAction = monsterAction;
    }

    public int getPlayerHp() {
        return playerHp;
    }

    public void setPlayerHp(int playerHp) {
        this.playerHp = playerHp;
    }

    public int getMaxPlayerHp() {
        return maxPlayerHp;
    }

    public void setMaxPlayerHp(int maxPlayerHp) {
        this.maxPlayerHp = maxPlayerHp;
    }

    public int getMonsterHp() {
        return monsterHp;
    }

    public void setMonsterHp(int monsterHp) {
        this.monsterHp = monsterHp;
    }

    public int getMaxMonsterHp() {
        return maxMonsterHp;
    }

    public void setMaxMonsterHp(int maxMonsterHp) {
        this.maxMonsterHp = maxMonsterHp;
    }

    public String getMonsterName() {
        return monsterName;
    }

    public void setMonsterName(String monsterName) {
        this.monsterName = monsterName;
    }

    public boolean isPlayerDead() {
        return isPlayerDead;
    }

    public void setPlayerDead(boolean playerDead) {
        this.isPlayerDead = playerDead;
    }

    public boolean isMonsterDead() {
        return isMonsterDead;
    }

    public void setMonsterDead(boolean monsterDead) {
        this.isMonsterDead = monsterDead;
    }

    public List<String> getLogMessages() {
        return logMessages;
    }

    public void addLogMessage(String msg) {
        this.logMessages.add(msg);
    }
}
