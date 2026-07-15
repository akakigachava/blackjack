package blackjack.persistence;

/** Report row: the highest bankroll a player has reached after any round. */
public class PlayerBankrollHigh {
    private String playerName;
    private int highestBankroll;

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getHighestBankroll() {
        return highestBankroll;
    }

    public void setHighestBankroll(int highestBankroll) {
        this.highestBankroll = highestBankroll;
    }
}
