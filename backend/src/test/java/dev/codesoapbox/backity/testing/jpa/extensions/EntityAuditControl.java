package dev.codesoapbox.backity.testing.jpa.extensions;

import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.auditing.AuditingHandler;

/// Controls whether JPA entity auditing is applied during persistence operations.
///
/// By default, Spring Data automatically populates fields annotated with
/// [CreatedDate] and [LastModifiedDate] on every save.
/// This makes it impossible to persist entities with custom timestamps, which is
/// often necessary when setting up test fixtures that represent historical data.
///
/// This class allows auditing to be temporarily disabled so that custom date values are preserved during persistence.
@RequiredArgsConstructor
public class EntityAuditControl {

    private final AuditingHandler auditingHandler;

    public void disable() {
        auditingHandler.setDateTimeForNow(false);
    }

    public void enable() {
        auditingHandler.setDateTimeForNow(true);
    }
}
