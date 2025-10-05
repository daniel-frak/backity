package dev.codesoapbox.backity.shared.application.events.outbox;

import dev.codesoapbox.backity.shared.domain.DomainEvent;

import java.util.UUID;

public record OutboxEvent(UUID id, DomainEvent domainEvent) {
}
