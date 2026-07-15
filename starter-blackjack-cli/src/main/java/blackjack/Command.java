package blackjack;

public enum Command {
    HIT, STAND, DOUBLE, SURRENDER, QUIT, INVALID;

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
        if (text.equals("double")) {
            return DOUBLE;
        }
        if (text.equals("surrender")) {
            return SURRENDER;
        }
        return INVALID;
    }
}
