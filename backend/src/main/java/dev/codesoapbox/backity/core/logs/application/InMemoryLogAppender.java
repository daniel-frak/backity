package dev.codesoapbox.backity.core.logs.application;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryLogAppender extends AppenderBase<ILoggingEvent> {

    @Getter
    private final Map<Long, ILoggingEvent> eventMap = new ConcurrentHashMap<>();

    @Override
    protected void append(ILoggingEvent event) {
        eventMap.put(System.currentTimeMillis(), event);
    }
}
