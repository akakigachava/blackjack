package blackjack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * A 52-card deck. Production play uses {@link #shuffled(Random)}, which
 * deals in random order and reshuffles the full deck when it runs low
 * (single-deck shoe). The no-argument and explicit-card constructors keep
 * the deterministic order and are used by tests.
 *
 * Fixed-order decks preserve the baseline quirk of returning a phantom
 * ace of hearts when exhausted; shuffled decks reshuffle instead.
 */
public class Deck {
    private static final Logger LOGGER = Logger.getLogger(Deck.class.getName());

    private static final String[] RANKS =
            {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
    private static final String[] SUITS = {"H", "D", "C", "S"};

    private final List<Card> cards = new ArrayList<>();
    private final Random shuffler;
    private int position = 0;

    public Deck() {
        this((Random) null);
    }

    private Deck(Random shuffler) {
        this.shuffler = shuffler;
        for (String suit : SUITS) {
            for (String rank : RANKS) {
                cards.add(new Card(rank, suit));
            }
        }
        if (shuffler != null) {
            Collections.shuffle(cards, shuffler);
        }
    }

    public Deck(String... cardCodes) {
        this.shuffler = null;
        for (String code : cardCodes) {
            cards.add(Card.fromCode(code));
        }
    }

    /** A full deck dealt in random order that reshuffles when it runs out. */
    public static Deck shuffled(Random random) {
        return new Deck(random);
    }

    public Card draw() {
        if (position >= cards.size()) {
            if (shuffler == null) {
                return Card.fromCode("AH");
            }
            reshuffle();
        }
        Card card = cards.get(position);
        position++;
        return card;
    }

    /**
     * Reshuffles the whole deck before a round if fewer than
     * {@code minCards} remain. Fixed-order decks are left untouched.
     */
    public void prepareForRound(int minCards) {
        if (shuffler != null && remaining() < minCards) {
            reshuffle();
        }
    }

    private void reshuffle() {
        Collections.shuffle(cards, shuffler);
        position = 0;
        LOGGER.info("Deck reshuffled");
    }

    public int remaining() {
        return cards.size() - position;
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
