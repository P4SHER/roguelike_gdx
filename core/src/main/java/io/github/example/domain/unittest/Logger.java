package io.github.example.domain.unittest;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final String LOG_FILE = "src/domain/unittest/log.txt";
    private static PrintWriter writer;
    private static boolean initialized = false;

    // Уровни логирования
    public enum LogType {
        INFO,      // Обычная информация
        DEBUG,     // Детали для отладки
        WARNING,   // Предупреждения
        ERROR      // Ошибки
    }

    // Инициализация (вызвать один раз при старте)
    public static void init() {
        try {
            // true = режим дозаписи (append), чтобы логи не стирались
            writer = new PrintWriter(new FileWriter(LOG_FILE, true));
            initialized = true;
            log("=== НОВАЯ ИГРОВАЯ СЕССИЯ ===", LogType.INFO);
        } catch (IOException e) {
            System.err.println("Не удалось инициализировать логгер: " + e.getMessage());
        }
    }

    // Публичный метод для записи
    public static void log(String message, LogType level) {
        if (!initialized) init();

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String logLine = String.format("[%s] [%s] %s", timestamp, level, message);

        // Пишем в файл
        if (writer != null) {
            writer.println(logLine);
            writer.flush(); // Сразу сбрасываем буфер
        }

    }

    // Удобные методы-сокращения
    public static void info(String msg) { log(msg, LogType.INFO); }
    public static void debug(String msg) { log(msg, LogType.DEBUG); }
    public static void warn(String msg) { log(msg, LogType.WARNING); }
    public static void error(String msg) { log(msg, LogType.ERROR); }

    // Закрытие потока (при выходе из игры)
    public static void close() {
        if (writer != null) {
            writer.close();
        }
    }
}
