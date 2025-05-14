package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryProgressChangedEvent;
import org.mapstruct.Mapper;

@Mapper
public interface GameContentDiscoveryProgressChangedWsEventMapper {

    GameContentDiscoveryProgressChangedWsEvent toWsEvent(GameContentDiscoveryProgressChangedEvent domain);
}
