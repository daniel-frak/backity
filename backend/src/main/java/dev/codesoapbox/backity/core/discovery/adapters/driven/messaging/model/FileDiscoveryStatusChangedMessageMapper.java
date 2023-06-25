package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.discovery.domain.messages.FileDiscoveryStatus;
import org.mapstruct.Mapper;

@Mapper
public interface FileDiscoveryStatusChangedMessageMapper {

    FileDiscoveryStatusChangedWsMessage toMessage(FileDiscoveryStatus domain);
}
