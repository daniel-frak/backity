package dev.codesoapbox.backity.core.files.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.files.domain.discovery.model.messages.FileDiscoveryStatus;
import org.mapstruct.Mapper;

@Mapper
public interface FileDiscoveryStatusHttpDtoMapper {

    FileDiscoveryStatusHttpDto toDto(FileDiscoveryStatus domain);
}
