package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStatusChangedEvent;
import org.mapstruct.Mapper;

@Mapper
public abstract class GameContentDiscoveryStatusChangedWsEventMapper {

    public abstract GameContentDiscoveryStatusChangedWsEvent toWsEvent(GameContentDiscoveryStatusChangedEvent domain);

    protected String getValue(GameProviderId id) {
        return id.value();
    }
}
