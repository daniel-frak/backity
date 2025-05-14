package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryStatus;
import org.mapstruct.Mapper;

@Mapper
public interface GameContentDiscoveryStatusHttpDtoMapper {

    GameContentDiscoveryStatusHttpDto toDto(GameContentDiscoveryStatus domain);
}
