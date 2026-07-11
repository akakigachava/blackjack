package blackjack;

import java.util.Objects;

public final class Card {
    private final String rank;
    private final String suit;

    public Card(String rank, String suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public static Card fromCode(String code) {
        return new Card(code.substring(0, code.length() - 1),
                code.substring(code.length() - 1));
    }

    public String rank() {
        return rank;
    }

    public String suit() {
        return suit;
    }

    @Override
    public String toString() {
        return rank + suit;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Card)) {
            return false;
        }
        Card card = (Card) other;
        return rank.equals(card.rank) && suit.equals(card.suit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rank, suit);
    }
}
