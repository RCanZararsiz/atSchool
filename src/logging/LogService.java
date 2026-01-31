package logging;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LogService implements LogSubject {
    private static LogService instance;
    private final String logFilePath;
    private final List<LogRecord> logs;
    private final DateTimeFormatter formatter;

    private LogService() {
        this.logFilePath = "admin_logs.txt";
        this.logs = new ArrayList<>();
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        loadLogs();
    }

    public static LogService getInstance() {
        if (instance == null) {
            instance = new LogService();
        }
        return instance;
    }

    public void log(String message, LogLevel level) {
        LogRecord logRecord = new LogRecord(message, level, LocalDateTime.now());
        logs.add(logRecord);
        saveLog(logRecord);
        notifyObservers(message, level);
    }

    private void saveLog(LogRecord logRecord) {
        try (FileWriter fw = new FileWriter(logFilePath, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(logRecord.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadLogs() {
        File file = new File(logFilePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 3) {
                    LocalDateTime timestamp = LocalDateTime.parse(parts[0].trim(), formatter);
                    LogLevel level = LogLevel.valueOf(parts[1].trim());
                    String message = parts[2].trim();
                    logs.add(new LogRecord(message, level, timestamp));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<LogRecord> getLogs() {
        return new ArrayList<>(logs);
    }

    public List<LogRecord> getLogsByLevel(LogLevel level) {
        return logs.stream()
                .filter(log -> log.getLevel() == level)
                .toList();
    }

    public void clearLogs() {
        logs.clear();
        try (PrintWriter writer = new PrintWriter(logFilePath)) {
            writer.print("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 