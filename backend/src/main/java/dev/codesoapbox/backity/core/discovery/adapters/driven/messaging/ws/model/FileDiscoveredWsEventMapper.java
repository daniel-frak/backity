package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveredEvent;
import dev.codesoapbox.backity.core.gamefile.domain.FileSize;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class FileDiscoveredWsEventMapper {

    public abstract FileDiscoveredWsEvent toWsEvent(FileDiscoveredEvent event);

    protected String toString(FileSize fileSize) {
        return fileSize.toString();
    }
}
