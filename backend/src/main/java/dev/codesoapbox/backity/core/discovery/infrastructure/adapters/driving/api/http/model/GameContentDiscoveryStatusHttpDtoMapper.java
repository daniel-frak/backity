package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryStatus;
import org.mapstruct.Mapper;

@Mapper
public abstract class GameContentDiscoveryStatusHttpDtoMapper {

    public abstract GameContentDiscoveryStatusHttpDto toDto(GameContentDiscoveryStatus domain);

    protected String getValue(GameProviderId id) {
        return id.value();
    }
}
