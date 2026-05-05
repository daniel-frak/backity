package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;

public class BackupHttpDtoMapper {

    public String getValue(GameProviderId gameProviderId) {
        return gameProviderId.value().toString();
    }
}
