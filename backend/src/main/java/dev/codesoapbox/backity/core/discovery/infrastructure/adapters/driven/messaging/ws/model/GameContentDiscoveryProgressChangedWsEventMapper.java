package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.BackupValueObjectWsDtoMapper;
import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryProgressChangedEvent;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.model.SharedWsDtoMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = SharedWsDtoMapperConfig.class,
        uses = {
                BackupValueObjectWsDtoMapper.class
        })
public interface GameContentDiscoveryProgressChangedWsEventMapper {

    @Mapping(target = "timeLeftSeconds", source = "timeLeft.seconds")
    GameContentDiscoveryProgressChangedWsEvent toWsEvent(GameContentDiscoveryProgressChangedEvent domain);
}
