package dev.codesoapbox.backity.core.logs.infrastructure.adapters.driven.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Collections.synchronizedList;

public class InMemoryLimitedLogbackAppender extends AppenderBase<ILoggingEvent> {

    private static final int MAX_LOGS_DEFAULT = 100;

    @Getter
    private final List<ILoggingEvent> events = synchronizedList(new LinkedList<>());

    private final List<Consumer<ILoggingEvent>> subscribers = synchronizedList(new LinkedList<>());

    @Getter
    @Setter
    private Integer maxLogs = MAX_LOGS_DEFAULT;

    @Override
    protected void append(ILoggingEvent event) {
        events.add(event);
        if (events.size() > maxLogs) {
            events.remove(0);
        }
        subscribers.forEach(s -> s.accept(event));
    }

    public void subscribe(Consumer<ILoggingEvent> subscription) {
        subscribers.add(subscription);
    }
}
