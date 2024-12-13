package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveryStatusChangedEvent;
import org.mapstruct.Mapper;

@Mapper
public interface FileDiscoveryStatusChangedWsEventMapper {

    FileDiscoveryStatusChangedWsEvent toWsEvent(FileDiscoveryStatusChangedEvent domain);
}
