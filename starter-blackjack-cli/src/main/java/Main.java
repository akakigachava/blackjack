import blackjack.Command;
import blackjack.ConsoleView;
import blackjack.Game;
import blackjack.LogSetup;
import blackjack.Outcome;

import java.util.Scanner;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger("blackjack.Main");

    public static void main(String[] args) {
        LogSetup.configure();
        LOGGER.info("Game started");

        Game game = new Game();
        ConsoleView view = new ConsoleView();
        Scanner input = new Scanner(System.in);

        game.startRound();
        view.showWelcome();

        while (true) {
            view.showTable(game, false);

            if (game.playerIsBust()) {
                view.showTable(game, true);
                view.showPlayerBust();
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
                game.playerHit();
                continue;
            }

            if (command == Command.STAND) {
                LOGGER.info("Player action: stand");
                game.dealerPlay();
                view.showTable(game, true);
                Outcome outcome = game.outcome();
                view.showOutcome(outcome);
                LOGGER.info(() -> "Round ended: " + outcome);
                break;
            }

            LOGGER.warning(() -> "Invalid input: \"" + rawInput.trim() + "\", treated as stand");
            view.showInvalidCommand();
            game.dealerPlay();
            view.showTable(game, true);
            Outcome outcome = game.outcome();
            view.showOutcome(outcome);
            LOGGER.info(() -> "Round ended: " + outcome);
            break;
        }
    }
}
