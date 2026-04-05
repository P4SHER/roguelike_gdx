package io.github.example.presentation.util;

/**
 * Lightweight logging facade for the presentation layer.
 * Thread-safe with optional ANSI color output.
 */
public class Logger {
    public enum Level {
        DEBUG,
        INFO,
        WARN,
        ERROR
    }

    private static final boolean DEBUG_MODE = Constants.DEBUG_MODE;
    private static final Object lock = new Object();

    // ANSI color codes
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_DEBUG = "\u001B[36m";    // Cyan
    private static final String ANSI_INFO = "\u001B[32m";     // Green
    private static final String ANSI_WARN = "\u001B[33m";     // Yellow
    private static final String ANSI_ERROR = "\u001B[31m";    // Red

    public static void debug(String message) {
        if (DEBUG_MODE) {
            log(Level.DEBUG, message);
        }
    }

    public static void info(String message) {
        log(Level.INFO, message);
    }

    public static void warn(String message) {
        log(Level.WARN, message);
    }

    public static void error(String message) {
        log(Level.ERROR, message);
    }

    public static void error(String message, Throwable exception) {
        synchronized (lock) {
            log(Level.ERROR, message);
            if (exception != null) {
                exception.printStackTrace(System.err);
            }
        }
    }

    private static void log(Level level, String message) {
        synchronized (lock) {
            String color = getColorForLevel(level);
            String levelName = level.toString();
            String timestamp = String.format("[%s]", System.currentTimeMillis() % 100000);
            String output = String.format("%s%s [%-5s] %s%s", color, timestamp, levelName, message, ANSI_RESET);
            
            if (level == Level.ERROR) {
                System.err.println(output);
            } else {
                System.out.println(output);
            }
        }
    }

    private static String getColorForLevel(Level level) {
        switch (level) {
            case DEBUG:
                return ANSI_DEBUG;
            case INFO:
                return ANSI_INFO;
            case WARN:
                return ANSI_WARN;
            case ERROR:
                return ANSI_ERROR;
            default:
                return ANSI_RESET;
        }
    }
}
