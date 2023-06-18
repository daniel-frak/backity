package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.discovery.domain.messages.FileDiscoveryProgress;
import org.mapstruct.Mapper;

@Mapper
public abstract class FileDiscoveryProgressUpdateMessageMapper {

    public abstract FileDiscoveryProgressUpdateMessage toMessage(FileDiscoveryProgress domain);
}
