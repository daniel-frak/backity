package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveryProgressChangedEvent;
import org.mapstruct.Mapper;

@Mapper
public interface FileDiscoveryProgressChangedWsEventMapper {

    FileDiscoveryProgressChangedWsEvent toWsEvent(FileDiscoveryProgressChangedEvent domain);
}
