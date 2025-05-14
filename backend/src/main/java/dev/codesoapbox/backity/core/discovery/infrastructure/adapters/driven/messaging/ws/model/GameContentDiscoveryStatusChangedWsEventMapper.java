package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStatusChangedEvent;
import org.mapstruct.Mapper;

@Mapper
public interface GameContentDiscoveryStatusChangedWsEventMapper {

    GameContentDiscoveryStatusChangedWsEvent toWsEvent(GameContentDiscoveryStatusChangedEvent domain);
}
