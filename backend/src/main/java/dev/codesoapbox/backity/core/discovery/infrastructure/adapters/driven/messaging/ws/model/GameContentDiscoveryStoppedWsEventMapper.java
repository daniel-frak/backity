package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.BackupValueObjectWsDtoMapper;
import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStoppedEvent;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.model.SharedWsDtoMapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = SharedWsDtoMapperConfig.class,
        uses = {
                BackupValueObjectWsDtoMapper.class
        })
public interface GameContentDiscoveryStoppedWsEventMapper {

    GameContentDiscoveryStoppedWsEvent toWsEvent(GameContentDiscoveryStoppedEvent domain);
}
