package dev.codesoapbox.backity.core.files.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.files.domain.discovery.model.messages.FileDiscoveryStatus;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FileDiscoveryStatusHttpDtoMapper {

    FileDiscoveryStatusHttpDtoMapper INSTANCE = Mappers.getMapper(FileDiscoveryStatusHttpDtoMapper.class);

    FileDiscoveryStatusHttpDto toDto(FileDiscoveryStatus domain);
}
