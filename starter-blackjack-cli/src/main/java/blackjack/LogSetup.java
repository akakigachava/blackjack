package blackjack;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class LogSetup {
    private static Logger packageLogger;
    private static boolean configured = false;

    private LogSetup() {
    }

    public static synchronized void configure() {
        if (configured) {
            return;
        }
        configured = true;

        packageLogger = Logger.getLogger("blackjack");
        packageLogger.setLevel(Level.INFO);
        packageLogger.setUseParentHandlers(false);
        try {
            FileHandler fileHandler = new FileHandler("blackjack.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            packageLogger.addHandler(fileHandler);
        } catch (IOException e) {
            System.err.println("File logging disabled: " + e.getMessage());
        }
    }
}
