import blackjack.Command;
import blackjack.ConsoleView;
import blackjack.Game;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
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
                break;
            }

            view.showActionPrompt();
            Command command = Command.parse(input.nextLine());

            if (command == Command.QUIT) {
                view.showGameStopped();
                break;
            }

            if (command == Command.HIT) {
                game.playerHit();
                continue;
            }

            if (command == Command.STAND) {
                game.dealerPlay();
                view.showTable(game, true);
                view.showOutcome(game.outcome());
                break;
            }

            view.showInvalidCommand();
            game.dealerPlay();
            view.showTable(game, true);
            view.showOutcome(game.outcome());
            break;
        }
    }
}
