package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;

public class BackupValueObjectJpaDtoMapper {

    public String getValue(GameProviderId gameProviderId) {
        return gameProviderId.value();
    }

    public GameProviderId toGameProviderId(String value) {
        return new GameProviderId(value);
    }
}
