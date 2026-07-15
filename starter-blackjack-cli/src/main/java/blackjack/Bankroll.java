package blackjack;

/** The player's chips across rounds of a session. */
public class Bankroll {
    private int chips;

    public Bankroll(int startingChips) {
        this.chips = startingChips;
    }

    public int chips() {
        return chips;
    }

    public boolean canAfford(int amount) {
        return amount <= chips;
    }

    public void apply(int delta) {
        chips += delta;
    }

    public boolean isEmpty() {
        return chips <= 0;
    }
}
