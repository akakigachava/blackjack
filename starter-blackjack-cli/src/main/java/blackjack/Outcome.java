package blackjack;

/**
 * The domain result of a finished round. Mapping to player-facing text
 * belongs to the view ({@link ConsoleView#displayText(Outcome)}).
 */
public enum Outcome {
    PLAYER_WINS,
    /** Natural blackjack: 21 with the first two cards, pays 3:2. */
    PLAYER_BLACKJACK,
    DEALER_WINS,
    PUSH,
    /** Player surrendered as their first action, forfeiting half the bet. */
    SURRENDER
}
