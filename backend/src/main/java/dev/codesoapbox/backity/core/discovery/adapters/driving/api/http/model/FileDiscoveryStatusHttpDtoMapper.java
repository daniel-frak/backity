package dev.codesoapbox.backity.core.discovery.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.discovery.domain.messages.FileDiscoveryStatus;
import org.mapstruct.Mapper;

@Mapper
public interface FileDiscoveryStatusHttpDtoMapper {

    FileDiscoveryStatusHttpDto toDto(FileDiscoveryStatus domain);
}
