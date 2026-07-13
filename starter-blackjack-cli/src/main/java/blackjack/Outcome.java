package blackjack;

/**
 * The domain result of a finished round. Mapping to player-facing text
 * belongs to the view ({@link ConsoleView#displayText(Outcome)}).
 */
public enum Outcome {
    PLAYER_WINS,
    DEALER_WINS,
    PUSH
}
