import blackjack.Bankroll;
import blackjack.Command;
import blackjack.ConsoleView;
import blackjack.Deck;
import blackjack.Game;
import blackjack.LogSetup;
import blackjack.Outcome;
import blackjack.ReportView;
import blackjack.Rules;
import blackjack.persistence.Database;
import blackjack.persistence.HistoryRepository;
import blackjack.persistence.SessionRecorder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger("blackjack.Main");
    private static final String DEFAULT_PLAYER_NAME = "Player";
    private static final int STARTING_CHIPS = 100;
    private static final int DEFAULT_BET = 10;

    public static void main(String[] args) {
        LogSetup.configure();

        if (hasFlag(args, "--stats")) {
            LOGGER.info("Stats report requested");
            showStats();
            return;
        }

        LOGGER.info("Game started");

        Game game = new Game(newShuffledDeck());
        ConsoleView view = new ConsoleView();
        Scanner input = new Scanner(System.in);
        Bankroll bankroll = new Bankroll(STARTING_CHIPS);

        try (SessionRecorder recorder = SessionRecorder.start(playerNameFrom(args))) {
            view.showWelcome();
            view.showBankroll(bankroll.chips());

            while (!bankroll.isEmpty()) {
                int bet = promptBet(input, view, bankroll.chips());
                if (bet < 0) {
                    view.showGameStopped();
                    LOGGER.info("Session ended: player quit at bet prompt");
                    break;
                }
                if (!playRound(game, view, input, recorder, bankroll, bet)) {
                    LOGGER.info("Session ended: player quit mid-round");
                    break;
                }
                if (bankroll.isEmpty()) {
                    view.showOutOfChips();
                    LOGGER.info("Session ended: out of chips");
                }
            }
        }
    }

    /** Plays one betted round. Returns false when the player quit mid-round. */
    private static boolean playRound(Game game, ConsoleView view, Scanner input,
                                     SessionRecorder recorder, Bankroll bankroll, int bet) {
        List<String> actions = new ArrayList<>();
        game.startRound();

        Outcome natural = game.naturalOutcome();
        if (natural != null) {
            view.showTable(game, true);
            settle(game, view, recorder, bankroll, bet, natural, actions, true);
            return true;
        }

        boolean firstAction = true;
        while (true) {
            view.showTable(game, false);

            if (game.playerIsBust()) {
                view.showTable(game, true);
                view.showPlayerBust();
                settle(game, view, recorder, bankroll, bet, game.outcome(), actions, false);
                return true;
            }

            view.showActionPrompt();
            String rawInput = readLine(input);
            if (rawInput == null || Command.parse(rawInput) == Command.QUIT) {
                view.showGameStopped();
                return false;
            }
            Command command = Command.parse(rawInput);

            if (command == Command.HIT) {
                LOGGER.info("Player action: hit");
                actions.add("HIT");
                game.playerHit();
                firstAction = false;
                continue;
            }

            if (command == Command.DOUBLE) {
                if (!firstAction) {
                    view.showActionNotAllowed("double");
                    continue;
                }
                if (!bankroll.canAfford(bet * 2)) {
                    view.showCannotAffordDouble();
                    continue;
                }
                LOGGER.info("Player action: double down");
                actions.add("DOUBLE");
                bet *= 2;
                game.playerHit();
                if (game.playerIsBust()) {
                    view.showTable(game, true);
                    view.showPlayerBust();
                    settle(game, view, recorder, bankroll, bet, game.outcome(), actions, false);
                    return true;
                }
                finishAgainstDealer(game, view, recorder, bankroll, bet, actions);
                return true;
            }

            if (command == Command.SURRENDER) {
                if (!firstAction) {
                    view.showActionNotAllowed("surrender");
                    continue;
                }
                LOGGER.info("Player action: surrender");
                actions.add("SURRENDER");
                view.showTable(game, true);
                settle(game, view, recorder, bankroll, bet, Outcome.SURRENDER, actions, true);
                return true;
            }

            if (command == Command.STAND) {
                LOGGER.info("Player action: stand");
                actions.add("STAND");
                finishAgainstDealer(game, view, recorder, bankroll, bet, actions);
                return true;
            }

            LOGGER.warning(() -> "Invalid input: \"" + rawInput.trim() + "\", treated as stand");
            view.showInvalidCommand();
            actions.add("STAND");
            finishAgainstDealer(game, view, recorder, bankroll, bet, actions);
            return true;
        }
    }

    private static void finishAgainstDealer(Game game, ConsoleView view, SessionRecorder recorder,
                                            Bankroll bankroll, int bet, List<String> actions) {
        game.dealerPlay();
        view.showTable(game, true);
        settle(game, view, recorder, bankroll, bet, game.outcome(), actions, true);
    }

    private static void settle(Game game, ConsoleView view, SessionRecorder recorder,
                               Bankroll bankroll, int bet, Outcome outcome,
                               List<String> actions, boolean showOutcomeLine) {
        if (showOutcomeLine) {
            view.showOutcome(outcome);
        }
        int delta = Rules.payout(outcome, bet);
        bankroll.apply(delta);
        view.showRoundResult(delta, bankroll.chips());
        recorder.recordRound(game, outcome, actions, bet, delta, bankroll.chips());
        LOGGER.info(() -> "Round ended: " + outcome + ", bet " + bet
                + ", change " + delta + ", chips " + bankroll.chips());
    }

    private static int promptBet(Scanner input, ConsoleView view, int chips) {
        int defaultBet = Math.min(DEFAULT_BET, chips);
        while (true) {
            view.showBetPrompt(chips, defaultBet);
            String line = readLine(input);
            if (line == null) {
                return -1;
            }
            line = line.trim();
            if (line.equals("q") || line.equals("quit")) {
                return -1;
            }
            if (line.isEmpty()) {
                return defaultBet;
            }
            try {
                int bet = Integer.parseInt(line);
                if (bet >= 1 && bet <= chips) {
                    return bet;
                }
            } catch (NumberFormatException ignored) {
                // fall through to the error message
            }
            view.showBetError(chips);
        }
    }

    /** Reads a line, or returns null when input is exhausted (treated as quit). */
    private static String readLine(Scanner input) {
        return input.hasNextLine() ? input.nextLine() : null;
    }

    /**
     * The production deck is randomly shuffled. Tests may pin the shuffle
     * with -Dblackjack.deck.seed=<long> to make CLI rounds reproducible.
     */
    private static Deck newShuffledDeck() {
        String seed = System.getProperty("blackjack.deck.seed");
        Random random = seed == null ? new Random() : new Random(Long.parseLong(seed));
        return Deck.shuffled(random);
    }

    private static void showStats() {
        try {
            HistoryRepository repository = new HistoryRepository(Database.fromEnvironment());
            new ReportView().showReports(repository);
        } catch (RuntimeException e) {
            System.out.println("Could not open the game database: " + e.getMessage());
            LOGGER.warning("Stats report failed: " + e);
        }
    }

    private static boolean hasFlag(String[] args, String flag) {
        for (String arg : args) {
            if (arg.equals(flag)) {
                return true;
            }
        }
        return false;
    }

    private static String playerNameFrom(String[] args) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals("--player") && !args[i + 1].isBlank()) {
                return args[i + 1].trim();
            }
        }
        return DEFAULT_PLAYER_NAME;
    }
}
