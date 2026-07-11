package blackjack;

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

    public String outcome() {
        return Rules.determineOutcome(playerHand.value(), dealerHand.value());
    }

    public Hand playerHand() {
        return playerHand;
    }

    public Hand dealerHand() {
        return dealerHand;
    }

    public Deck deck() {
        return deck;
    }

    private void dealTo(Hand hand, String recipient) {
        Card card = deck.draw();
        hand.add(card);
        LOGGER.info(() -> "Card dealt to " + recipient + ": " + card);
    }
}
