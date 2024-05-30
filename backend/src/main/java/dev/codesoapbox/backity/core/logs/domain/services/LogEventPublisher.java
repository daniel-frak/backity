package dev.codesoapbox.backity.core.logs.domain.services;

import dev.codesoapbox.backity.core.logs.domain.model.LogCreatedEvent;

public interface LogEventPublisher {

    void publish(LogCreatedEvent payload);
}