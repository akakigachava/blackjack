package blackjack;

import blackjack.persistence.HistoryRepository;
import blackjack.persistence.PlayerOutcomes;
import blackjack.persistence.PlayerRoundAverage;
import blackjack.persistence.RoundHistoryEntry;
import blackjack.persistence.SessionSummary;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/** Renders the persisted game history as CLI reports (--stats mode). */
public class ReportView {
    private static final DateTimeFormatter TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final int DEFAULT_LIMIT = 10;

    public void showReports(HistoryRepository repository) {
        showRecentSessions(repository.recentSessions(DEFAULT_LIMIT));
        showPlayerOutcomes(repository.playerOutcomeCounts());
        showRoundAverages(repository.averageRoundsPerSession());
        showRecentRounds(repository.recentRounds(DEFAULT_LIMIT));
    }

    private void showRecentSessions(List<SessionSummary> sessions) {
        System.out.println("=== Recent sessions (latest " + DEFAULT_LIMIT + ") ===");
        if (sessions.isEmpty()) {
            System.out.println("No sessions recorded yet.");
        }
        for (SessionSummary s : sessions) {
            String ended = s.getEndedAt() == null ? "not ended" : TIME.format(s.getEndedAt());
            System.out.println("Session " + s.getSessionId() + ": " + s.getPlayerName()
                    + ", started " + TIME.format(s.getStartedAt())
                    + ", ended " + ended
                    + ", rounds " + s.getRoundsPlayed());
        }
        System.out.println();
    }

    private void showPlayerOutcomes(List<PlayerOutcomes> outcomes) {
        System.out.println("=== Player results ===");
        if (outcomes.isEmpty()) {
            System.out.println("No completed rounds recorded yet.");
        }
        for (PlayerOutcomes o : outcomes) {
            System.out.println(o.getPlayerName() + ": " + o.getWins() + " wins, "
                    + o.getLosses() + " losses, " + o.getPushes() + " pushes"
                    + " (" + o.getTotalRounds() + " rounds)");
        }
        System.out.println();
    }

    private void showRoundAverages(List<PlayerRoundAverage> averages) {
        System.out.println("=== Rounds per session ===");
        if (averages.isEmpty()) {
            System.out.println("No sessions recorded yet.");
        }
        for (PlayerRoundAverage a : averages) {
            System.out.println(a.getPlayerName() + ": " + a.getRoundCount()
                    + " rounds in " + a.getSessionCount() + " sessions"
                    + " (avg " + String.format(Locale.ROOT, "%.1f", a.getAverageRoundsPerSession())
                    + " per session)");
        }
        System.out.println();
    }

    private void showRecentRounds(List<RoundHistoryEntry> rounds) {
        System.out.println("=== Recent rounds (latest " + DEFAULT_LIMIT + ") ===");
        if (rounds.isEmpty()) {
            System.out.println("No completed rounds recorded yet.");
        }
        for (RoundHistoryEntry r : rounds) {
            System.out.println("[" + TIME.format(r.getPlayedAt()) + "] " + r.getPlayerName()
                    + " session " + r.getSessionId() + " round " + r.getRoundNumber()
                    + ": " + r.getPlayerCards() + " (" + r.getPlayerValue() + ")"
                    + " vs " + r.getDealerCards() + " (" + r.getDealerValue() + ")"
                    + " -> " + r.getOutcome());
        }
    }
}
