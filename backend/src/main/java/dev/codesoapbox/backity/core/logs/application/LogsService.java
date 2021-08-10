package dev.codesoapbox.backity.core.logs.application;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import lombok.SneakyThrows;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Service
public class LogsService {

    private final String logPath;
    private InMemoryLogAppender logAppender;

    public LogsService(@Value("${logging.file.name}") String logPath) {
        this.logPath = logPath;
        createLogger();
    }

    @SneakyThrows
    public List<String> getLogs() {
        return logAppender.getEventMap().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> getLogMessage(e))
                .collect(toList());
    }

    private String getLogMessage(Map.Entry<Long, ILoggingEvent> e) {
        return "[" + LocalDateTime.ofInstant(Instant.ofEpochMilli(e.getKey()), ZoneId.systemDefault()).toString() + "] " + e.getValue().getLevel().toString() + " " + e.getValue().getMessage();
    }

    private Logger createLogger() {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

        InMemoryLogAppender appender = new InMemoryLogAppender();
        appender.setContext(lc);
        appender.start();

        Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.addAppender(appender);
        logger.setLevel(Level.DEBUG);
        logger.setAdditive(false); /* set to true if root should log too */

        this.logAppender = appender;

        return logger;
    }
}
