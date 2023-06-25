package dev.codesoapbox.backity.core.logs.adapters.driven.logback;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import dev.codesoapbox.backity.core.logs.domain.model.LogCreatedMessage;
import dev.codesoapbox.backity.core.logs.domain.services.LogMessageService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@Slf4j
@ExtendWith(MockitoExtension.class)
class LogbackLogServiceIT {

    private static final int MAX_LOGS = 2;
    private static Appender<ILoggingEvent> originalConsoleAppender;

    private LogbackLogService logService;

    @Mock
    private LogMessageService messageService;

    @BeforeAll
    static void beforeAll() {
        addConsoleAppender();
    }

    private static void addConsoleAppender() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
        consoleAppender.setContext(loggerContext);
        consoleAppender.setName(LogbackLogService.CONSOLE_APPENDER);
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setPattern("%m");
        consoleAppender.setEncoder(encoder);
        consoleAppender.start();

        Logger logger = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        originalConsoleAppender = logger.getAppender(LogbackLogService.CONSOLE_APPENDER);
        logger.detachAppender(LogbackLogService.CONSOLE_APPENDER);
        logger.addAppender(consoleAppender);
    }

    @BeforeEach
    void setUp() {
        logService = new LogbackLogService(messageService, MAX_LOGS);
    }

    @AfterEach
    void tearDown() {
        if (originalConsoleAppender != null) {
            Logger logger = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
            logger.addAppender(originalConsoleAppender);
        }
    }

    @Test
    void shouldSendLogsToMessageService() {
        String logMessage = "Test log";

        log.info(logMessage);

        verify(messageService).sendLogCreated(LogCreatedMessage.of(logMessage, MAX_LOGS));
    }

    @Test
    void shouldGetLogs() {
        String logMessage = "Test log";

        log.info(logMessage);

        assertEquals(1, logService.getLogs().size());
        assertEquals(logMessage, logService.getLogs().get(0));
    }
}