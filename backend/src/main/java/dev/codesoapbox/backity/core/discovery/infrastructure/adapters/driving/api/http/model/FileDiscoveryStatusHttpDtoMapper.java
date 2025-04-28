package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.discovery.application.FileDiscoveryStatus;
import org.mapstruct.Mapper;

@Mapper
public interface FileDiscoveryStatusHttpDtoMapper {

    FileDiscoveryStatusHttpDto toDto(FileDiscoveryStatus domain);
}
