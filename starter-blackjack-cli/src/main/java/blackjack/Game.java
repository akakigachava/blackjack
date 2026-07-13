package blackjack;

import java.util.List;
import java.util.logging.Logger;

public class Game {
    private static final Logger LOGGER = Logger.getLogger(Game.class.getName());

    private final Deck deck;
    private Hand playerHand = new Hand();
    private Hand dealerHand = new Hand();

    public Game() {
        this(new Deck());
    }

    public Game(Deck deck) {
        this.deck = deck;
    }

    /**
     * Fixture constructor for tests: starts a game with pre-built hands
     * instead of dealing from the deck. Production code uses
     * {@link #startRound()}.
     */
    public Game(Deck deck, Hand playerHand, Hand dealerHand) {
        this.deck = deck;
        this.playerHand = playerHand;
        this.dealerHand = dealerHand;
    }

    public void startRound() {
        LOGGER.info("Round started");
        playerHand = new Hand();
        dealerHand = new Hand();
        dealTo(playerHand, "player");
        dealTo(dealerHand, "dealer");
        dealTo(playerHand, "player");
        dealTo(dealerHand, "dealer");
    }

    public void playerHit() {
        dealTo(playerHand, "player");
    }

    public void dealerPlay() {
        while (Rules.dealerShouldDraw(dealerHand.value())) {
            dealTo(dealerHand, "dealer");
        }
        LOGGER.info(() -> "Dealer action: stand at " + dealerHand.value());
    }

    public boolean playerIsBust() {
        return playerHand.isBust();
    }

    public Outcome outcome() {
        return Rules.determineOutcome(playerHand.value(), dealerHand.value());
    }

    public List<Card> playerCards() {
        return playerHand.cards();
    }

    public List<Card> dealerCards() {
        return dealerHand.cards();
    }

    public int playerValue() {
        return playerHand.value();
    }

    public int dealerValue() {
        return dealerHand.value();
    }

    private void dealTo(Hand hand, String recipient) {
        Card card = deck.draw();
        hand.add(card);
        LOGGER.info(() -> "Card dealt to " + recipient + ": " + card);
    }
}
