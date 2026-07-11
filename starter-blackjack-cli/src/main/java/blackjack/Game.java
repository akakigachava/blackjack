package blackjack;

public class Game {
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
        playerHand = new Hand();
        dealerHand = new Hand();
        playerHand.add(deck.draw());
        dealerHand.add(deck.draw());
        playerHand.add(deck.draw());
        dealerHand.add(deck.draw());
    }

    public void playerHit() {
        playerHand.add(deck.draw());
    }

    public void dealerPlay() {
        while (Rules.dealerShouldDraw(dealerHand.value())) {
            dealerHand.add(deck.draw());
        }
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
}
