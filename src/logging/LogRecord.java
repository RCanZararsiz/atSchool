package logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogRecord {
    private final String message;
    private final LogLevel level;
    private final LocalDateTime timestamp;
    private final DateTimeFormatter formatter;

    public LogRecord(String message, LogLevel level, LocalDateTime timestamp) {
        this.message = message;
        this.level = level;
        this.timestamp = timestamp;
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    public String getMessage() {
        return message;
    }

    public LogLevel getLevel() {
        return level;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %s",
                timestamp.format(formatter),
                level.name(),
                message);
    }
} 