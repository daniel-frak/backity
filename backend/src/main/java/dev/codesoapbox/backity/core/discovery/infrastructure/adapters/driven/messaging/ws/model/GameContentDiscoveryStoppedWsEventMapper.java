package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStoppedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class GameContentDiscoveryStoppedWsEventMapper {

    public abstract GameContentDiscoveryStoppedWsEvent toWsEvent(GameContentDiscoveryStoppedEvent domain);

    protected String getValue(GameProviderId id) {
        return id.value();
    }
}
