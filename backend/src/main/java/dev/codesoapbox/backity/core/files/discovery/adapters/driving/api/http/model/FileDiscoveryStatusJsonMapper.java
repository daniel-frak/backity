package dev.codesoapbox.backity.core.files.discovery.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.files.discovery.domain.model.messages.FileDiscoveryStatus;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FileDiscoveryStatusJsonMapper {

    FileDiscoveryStatusJsonMapper INSTANCE = Mappers.getMapper(FileDiscoveryStatusJsonMapper.class);

    FileDiscoveryStatusJson toJson(FileDiscoveryStatus domain);
}
