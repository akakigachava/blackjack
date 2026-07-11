package blackjack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Hand {
    private final List<Card> cards = new ArrayList<>();

    public void add(Card card) {
        cards.add(card);
    }

    public int cardCount() {
        return cards.size();
    }

    public Card cardAt(int index) {
        return cards.get(index);
    }

    public List<Card> cards() {
        return Collections.unmodifiableList(cards);
    }

    public int value() {
        int total = 0;
        int aces = 0;

        for (Card card : cards) {
            String rank = card.rank();
            if (rank.equals("A")) {
                total += 11;
                aces++;
            } else if (rank.equals("K") || rank.equals("Q") || rank.equals("J")) {
                total += 10;
            } else {
                total += Integer.parseInt(rank);
            }
        }

        while (total > 21 && aces > 0) {
            total -= 10;
            aces--;
        }

        return total;
    }

    public boolean isBust() {
        return value() > 21;
    }
}
