package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.discovery.domain.messages.FileDiscoveryProgress;
import org.mapstruct.Mapper;

@Mapper
public interface FileDiscoveryProgressUpdateMessageMapper {

    FileDiscoveryProgressUpdateWsMessage toMessage(FileDiscoveryProgress domain);
}
