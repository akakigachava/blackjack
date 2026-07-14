package blackjack.persistence;

import java.time.LocalDateTime;

/** Report row: one played round with its player, hands, and outcome. */
public class RoundHistoryEntry {
    private String playerName;
    private long sessionId;
    private int roundNumber;
    private String playerCards;
    private String dealerCards;
    private int playerValue;
    private int dealerValue;
    private String outcome;
    private LocalDateTime playedAt;

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public String getPlayerCards() {
        return playerCards;
    }

    public void setPlayerCards(String playerCards) {
        this.playerCards = playerCards;
    }

    public String getDealerCards() {
        return dealerCards;
    }

    public void setDealerCards(String dealerCards) {
        this.dealerCards = dealerCards;
    }

    public int getPlayerValue() {
        return playerValue;
    }

    public void setPlayerValue(int playerValue) {
        this.playerValue = playerValue;
    }

    public int getDealerValue() {
        return dealerValue;
    }

    public void setDealerValue(int dealerValue) {
        this.dealerValue = dealerValue;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(LocalDateTime playedAt) {
        this.playedAt = playedAt;
    }
}
