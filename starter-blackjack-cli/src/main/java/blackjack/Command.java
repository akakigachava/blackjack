package blackjack;

public enum Command {
    HIT, STAND, QUIT, INVALID;

    public static Command parse(String rawInput) {
        String text = rawInput.trim();
        if (text.equals("q") || text.equals("quit")) {
            return QUIT;
        }
        if (text.equals("hit")) {
            return HIT;
        }
        if (text.equals("stand")) {
            return STAND;
        }
        return INVALID;
    }
}
