package blackjack.persistence;

import java.time.LocalDateTime;

/**
 * Row in the rounds table: the final state of one completed round.
 * Cards are stored as space-separated codes ("AH 3H"), the outcome as
 * the {@link blackjack.Outcome} enum name.
 */
public class RoundRecord {
    private Long id;
    private long sessionId;
    private int roundNumber;
    private String playerCards;
    private String dealerCards;
    private int playerValue;
    private int dealerValue;
    private String outcome;
    private int bet;
    private int bankrollChange;
    private int bankrollAfter;
    private LocalDateTime playedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public int getBankrollChange() {
        return bankrollChange;
    }

    public void setBankrollChange(int bankrollChange) {
        this.bankrollChange = bankrollChange;
    }

    public int getBankrollAfter() {
        return bankrollAfter;
    }

    public void setBankrollAfter(int bankrollAfter) {
        this.bankrollAfter = bankrollAfter;
    }

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(LocalDateTime playedAt) {
        this.playedAt = playedAt;
    }
}
