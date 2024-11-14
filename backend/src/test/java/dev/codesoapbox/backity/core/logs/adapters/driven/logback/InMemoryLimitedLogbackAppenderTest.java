package dev.codesoapbox.backity.core.logs.adapters.driven.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryLimitedLogbackAppenderTest {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

    private InMemoryLimitedLogbackAppender inMemoryLimitedLogbackAppender;

    @BeforeEach
    void setUp() {
        inMemoryLimitedLogbackAppender = new InMemoryLimitedLogbackAppender();
    }

    @Test
    void shouldAppend() {
        LoggingEvent loggingEvent = new LoggingEvent(null, LOGGER,
                Level.INFO, "someMessage", new RuntimeException(),
                null);
        inMemoryLimitedLogbackAppender.append(loggingEvent);

        assertThat(inMemoryLimitedLogbackAppender.getEvents().getFirst()).isEqualTo(loggingEvent);
    }

    @Test
    void shouldAppendWithLimit() {
        inMemoryLimitedLogbackAppender.setMaxLogs(2);

        LoggingEvent loggingEvent1 = new LoggingEvent(null, LOGGER,
                Level.INFO, "someMessage1", new RuntimeException(),
                null);
        LoggingEvent loggingEvent2 = new LoggingEvent(null, LOGGER,
                Level.INFO, "someMessage2", new RuntimeException(),
                null);
        LoggingEvent loggingEvent3 = new LoggingEvent(null, LOGGER,
                Level.INFO, "someMessage3", null, null);

        inMemoryLimitedLogbackAppender.append(loggingEvent1);
        inMemoryLimitedLogbackAppender.append(loggingEvent2);
        inMemoryLimitedLogbackAppender.append(loggingEvent3);

        List<LoggingEvent> expectedEvents = List.of(loggingEvent2, loggingEvent3);

        assertThat(inMemoryLimitedLogbackAppender.getEvents()).isEqualTo(expectedEvents);
    }

    @Test
    void shouldSubscribe() {
        String expectedMessage = "someMessage";
        LoggingEvent loggingEvent = new LoggingEvent(null, LOGGER,
                Level.INFO, expectedMessage, new RuntimeException(),
                null);

        AtomicReference<String> message = new AtomicReference<>();
        inMemoryLimitedLogbackAppender.subscribe(e -> message.set(e.getMessage()));
        inMemoryLimitedLogbackAppender.append(loggingEvent);

        assertThat(message.get()).isEqualTo(expectedMessage);
    }
}