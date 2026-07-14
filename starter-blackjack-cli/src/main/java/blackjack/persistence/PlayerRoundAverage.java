package blackjack.persistence;

/** Report row: how many rounds a player plays per session on average. */
public class PlayerRoundAverage {
    private String playerName;
    private int sessionCount;
    private int roundCount;
    private double averageRoundsPerSession;

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getSessionCount() {
        return sessionCount;
    }

    public void setSessionCount(int sessionCount) {
        this.sessionCount = sessionCount;
    }

    public int getRoundCount() {
        return roundCount;
    }

    public void setRoundCount(int roundCount) {
        this.roundCount = roundCount;
    }

    public double getAverageRoundsPerSession() {
        return averageRoundsPerSession;
    }

    public void setAverageRoundsPerSession(double averageRoundsPerSession) {
        this.averageRoundsPerSession = averageRoundsPerSession;
    }
}
