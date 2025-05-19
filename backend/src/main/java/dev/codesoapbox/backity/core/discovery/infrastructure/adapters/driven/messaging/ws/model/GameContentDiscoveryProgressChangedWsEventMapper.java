package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryProgressChangedEvent;
import org.mapstruct.Mapper;

@Mapper
public abstract class GameContentDiscoveryProgressChangedWsEventMapper {

    public abstract GameContentDiscoveryProgressChangedWsEvent toWsEvent(
            GameContentDiscoveryProgressChangedEvent domain);

    protected String getValue(GameProviderId id) {
        return id.value();
    }
}
