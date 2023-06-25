package dev.codesoapbox.backity.core.logs.adapters.driven.logback;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;
import dev.codesoapbox.backity.core.logs.domain.model.LogCreatedMessage;
import dev.codesoapbox.backity.core.logs.domain.services.LogMessageService;
import dev.codesoapbox.backity.core.logs.domain.services.LogService;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

public class LogbackLogService implements LogService {

    static final String CONSOLE_APPENDER = "CONSOLE";

    // https://stackoverflow.com/a/25189932
    private static final Pattern ANSI_PATTERN = Pattern.compile("\\e\\[[\\d;]*[^\\d;]");

    private final LogMessageService messageService;
    private final InMemoryLimitedLogbackAppender logAppender;
    private final PatternLayout layout;

    @SuppressWarnings("squid:S1312")
    public LogbackLogService(LogMessageService messageService, Integer maxLogs) {
        this.messageService = messageService;
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = createLogger();
        this.layout = createPatternLayout(loggerContext, logger);
        this.logAppender = createAndAddLogAppender(loggerContext, logger, maxLogs);
        this.logAppender.subscribe(this::onLogEvent);
    }

    private void onLogEvent(ILoggingEvent event) {
        messageService.sendLogCreated(LogCreatedMessage.of(getLogMessage(event), logAppender.getMaxLogs()));
    }

    private Logger createLogger() {
        return (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
    }

    @SuppressWarnings("squid:S4792")
    private InMemoryLimitedLogbackAppender createAndAddLogAppender(LoggerContext loggerContext, Logger logger,
                                                                   Integer maxLogs) {
        InMemoryLimitedLogbackAppender inMemoryLogAppender = createAppender(loggerContext, maxLogs);
        logger.addAppender(inMemoryLogAppender);
        return inMemoryLogAppender;
    }

    private InMemoryLimitedLogbackAppender createAppender(LoggerContext loggerContext, Integer maxLogs) {
        InMemoryLimitedLogbackAppender appender = new InMemoryLimitedLogbackAppender();
        appender.setContext(loggerContext);
        appender.setMaxLogs(maxLogs);
        appender.start();

        return appender;
    }

    private PatternLayout createPatternLayout(LoggerContext loggerContext, Logger logger) {
        String pattern = getFileAppenderPattern(logger);

        PatternLayout patternLayout = new PatternLayout();
        patternLayout.setPattern(pattern);
        patternLayout.setContext(loggerContext);
        patternLayout.start();

        return patternLayout;
    }

    private String getFileAppenderPattern(Logger logger) {
        OutputStreamAppender<ILoggingEvent> fileAppender =
                (OutputStreamAppender<ILoggingEvent>) logger.getAppender(CONSOLE_APPENDER);

        PatternLayoutEncoder encoder = (PatternLayoutEncoder) fileAppender.getEncoder();
        return encoder.getPattern();
    }

    @Override
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
