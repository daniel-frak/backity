package dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;

public class BackupTargetValueObjectWsDtoMapper {

    public String getValue(BackupTargetId id) {
        return id.value().toString();
    }
}
