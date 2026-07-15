import blackjack.Command;
import blackjack.ConsoleView;
import blackjack.Deck;
import blackjack.Game;
import blackjack.LogSetup;
import blackjack.Outcome;
import blackjack.ReportView;
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

    public static void main(String[] args) {
        LogSetup.configure();

        if (hasFlag(args, "--stats")) {
            LOGGER.info("Stats report requested");
            showStats();
            return;
        }

        LOGGER.info("Game started");

        Game game = new Game(Deck.shuffled(new Random()));
        ConsoleView view = new ConsoleView();
        Scanner input = new Scanner(System.in);
        List<String> actions = new ArrayList<>();

        try (SessionRecorder recorder = SessionRecorder.start(playerNameFrom(args))) {
            game.startRound();
            view.showWelcome();

            while (true) {
                view.showTable(game, false);

                if (game.playerIsBust()) {
                    view.showTable(game, true);
                    view.showPlayerBust();
                    recorder.recordRound(game, game.outcome(), actions);
                    LOGGER.info("Round ended: player bust, dealer wins");
                    break;
                }

                view.showActionPrompt();
                String rawInput = input.nextLine();
                Command command = Command.parse(rawInput);

                if (command == Command.QUIT) {
                    view.showGameStopped();
                    LOGGER.info("Round ended: game stopped by player");
                    break;
                }

                if (command == Command.HIT) {
                    LOGGER.info("Player action: hit");
                    actions.add("HIT");
                    game.playerHit();
                    continue;
                }

                if (command == Command.STAND) {
                    LOGGER.info("Player action: stand");
                    actions.add("STAND");
                    finishRound(game, view, recorder, actions);
                    break;
                }

                LOGGER.warning(() -> "Invalid input: \"" + rawInput.trim() + "\", treated as stand");
                view.showInvalidCommand();
                actions.add("STAND");
                finishRound(game, view, recorder, actions);
                break;
            }
        }
    }

    private static void finishRound(Game game, ConsoleView view,
                                    SessionRecorder recorder, List<String> actions) {
        game.dealerPlay();
        view.showTable(game, true);
        Outcome outcome = game.outcome();
        view.showOutcome(outcome);
        recorder.recordRound(game, outcome, actions);
        LOGGER.info(() -> "Round ended: " + outcome);
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
