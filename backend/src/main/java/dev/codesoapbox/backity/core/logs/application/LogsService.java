package dev.codesoapbox.backity.core.logs.application;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;
import dev.codesoapbox.backity.core.logs.application.messages.LogCreatedMessage;
import dev.codesoapbox.backity.core.shared.application.MessageTopics;
import dev.codesoapbox.backity.core.shared.application.services.MessageService;
import lombok.SneakyThrows;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

@Service
public class LogsService {

    private static final String CONSOLE_APPENDER = "CONSOLE";

    // https://stackoverflow.com/a/25189932
    private static final Pattern ANSI_PATTERN = Pattern.compile("\\e\\[[\\d;]*[^\\d;]");

    private final MessageService messageService;
    private final InMemoryLimitedLogAppender logAppender;
    private final PatternLayout layout;

    public LogsService(MessageService messageService, @Value("${in-memory-logs.max}") Integer maxLogs) {
        this.messageService = messageService;
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = createLogger();
        this.layout = createPatternLayout(loggerContext, logger);
        this.logAppender = createAndAddLogAppender(loggerContext, logger, maxLogs);
        this.logAppender.subscribe(this::onLogEvent);
    }

    private void onLogEvent(ILoggingEvent event) {
        messageService.sendMessage(MessageTopics.LOGS.toString(),
                LogCreatedMessage.of(getLogMessage(event), logAppender.getMaxLogs()));
    }

    private Logger createLogger() {
        return (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    }

    private InMemoryLimitedLogAppender createAndAddLogAppender(LoggerContext loggerContext, Logger logger, Integer maxLogs) {
        InMemoryLimitedLogAppender logAppender = createAppender(loggerContext, maxLogs);
        logger.addAppender(logAppender);
        return logAppender;
    }

    private InMemoryLimitedLogAppender createAppender(LoggerContext loggerContext, Integer maxLogs) {
        InMemoryLimitedLogAppender appender = new InMemoryLimitedLogAppender();
        appender.setContext(loggerContext);
        appender.setMaxLogs(maxLogs);
        appender.start();

        return appender;
    }


    private PatternLayout createPatternLayout(LoggerContext loggerContext, Logger logger) {
        String pattern = getFileAppenderPattern(logger);

        PatternLayout layout = new PatternLayout();
        layout.setPattern(pattern);
        layout.setContext(loggerContext);
        layout.start();

        return layout;
    }

    private String getFileAppenderPattern(Logger logger) {
        OutputStreamAppender<ILoggingEvent> fileAppender =
                (OutputStreamAppender<ILoggingEvent>) logger.getAppender(CONSOLE_APPENDER);

        PatternLayoutEncoder encoder = (PatternLayoutEncoder) fileAppender.getEncoder();
        return encoder.getPattern();
    }

    @SneakyThrows
    public List<String> getLogs() {
        return logAppender.getEvents().stream()
                .map(this::getLogMessage)
                .collect(toList());
    }

    private String getLogMessage(ILoggingEvent event) {
        String message = layout.doLayout(event);
        return stripAnsi(message);
    }

    private String stripAnsi(String value) {
        return ANSI_PATTERN.matcher(value).replaceAll("");
    }
}
