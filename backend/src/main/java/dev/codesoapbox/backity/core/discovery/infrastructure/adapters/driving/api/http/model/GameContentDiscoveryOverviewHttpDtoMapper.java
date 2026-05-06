package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.api.http.model.BackupValueObjectHttpDtoMapper;
import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryOverview;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryProgress;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.SharedHttpDtoMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = SharedHttpDtoMapperConfig.class,
        uses = {
                BackupValueObjectHttpDtoMapper.class
        })
public interface GameContentDiscoveryOverviewHttpDtoMapper {

    GameContentDiscoveryOverviewHttpDto toDto(GameContentDiscoveryOverview domain);

    @Mapping(target = "timeLeftSeconds", source = "timeLeft.seconds")
    GameContentDiscoveryProgressHttpDto toProgressDto(GameContentDiscoveryProgress domain);
}
