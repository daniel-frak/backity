package dev.codesoapbox.backity.core.backup.infrastructure.adapters.shared.messaging.spring.outbox;

public record FileCopyEnqueuedOutboxEvent(
        String fileCopyId
) {
}
