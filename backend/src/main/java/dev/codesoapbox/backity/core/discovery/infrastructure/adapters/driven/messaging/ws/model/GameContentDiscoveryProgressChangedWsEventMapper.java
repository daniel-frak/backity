package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.api.http.model.BackupValueObjectHttpDtoMapper;
import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryProgressChangedEvent;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.model.SharedWsDtoMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = SharedWsDtoMapperConfig.class,
        uses = {
                BackupValueObjectHttpDtoMapper.class
        })
public abstract class GameContentDiscoveryProgressChangedWsEventMapper {

    @Mapping(target = "timeLeftSeconds", source = "timeLeft.seconds")
    public abstract GameContentDiscoveryProgressChangedWsEvent toWsEvent(
            GameContentDiscoveryProgressChangedEvent domain);
}
