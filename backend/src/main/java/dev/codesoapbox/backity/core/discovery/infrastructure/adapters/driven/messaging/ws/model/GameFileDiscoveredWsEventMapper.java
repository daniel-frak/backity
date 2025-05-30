package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.discovery.domain.events.GameFileDiscoveredEvent;
import dev.codesoapbox.backity.core.gamefile.domain.FileSize;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class GameFileDiscoveredWsEventMapper {

    public abstract GameFileDiscoveredWsEvent toWsEvent(GameFileDiscoveredEvent event);

    protected String toString(FileSize fileSize) {
        return fileSize.toString();
    }
}
