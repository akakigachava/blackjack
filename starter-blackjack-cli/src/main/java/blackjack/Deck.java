package blackjack;

import java.util.ArrayList;
import java.util.List;

/**
 * The baseline deck: 52 cards in a fixed, unshuffled order. Drawing past
 * the last card returns a phantom ace of hearts without advancing, which
 * preserves the starter implementation's behavior.
 */
public class Deck {
    private static final String[] RANKS =
            {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
    private static final String[] SUITS = {"H", "D", "C", "S"};

    private final List<Card> cards = new ArrayList<>();
    private int position = 0;

    public Deck() {
        for (String suit : SUITS) {
            for (String rank : RANKS) {
                cards.add(new Card(rank, suit));
            }
        }
    }

    public Deck(String... cardCodes) {
        for (String code : cardCodes) {
            cards.add(Card.fromCode(code));
        }
    }

    public Card draw() {
        if (position >= cards.size()) {
            return Card.fromCode("AH");
        }
        Card card = cards.get(position);
        position++;
        return card;
    }

    public int size() {
        return cards.size();
    }

    public int position() {
        return position;
    }

    public Card cardAt(int index) {
        return cards.get(index);
    }
}
