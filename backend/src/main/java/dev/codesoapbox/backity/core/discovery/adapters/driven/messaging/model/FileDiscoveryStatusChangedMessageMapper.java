package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.discovery.domain.messages.FileDiscoveryStatus;
import org.mapstruct.Mapper;

@Mapper
public abstract class FileDiscoveryStatusChangedMessageMapper {

    public abstract FileDiscoveryStatusChangedMessage toMessage(FileDiscoveryStatus domain);
}
