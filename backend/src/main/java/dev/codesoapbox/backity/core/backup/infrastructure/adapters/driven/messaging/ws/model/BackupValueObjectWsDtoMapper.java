package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;

public class BackupValueObjectWsDtoMapper {

    public String getValue(GameProviderId gameProviderId) {
        return gameProviderId.value();
    }
}
