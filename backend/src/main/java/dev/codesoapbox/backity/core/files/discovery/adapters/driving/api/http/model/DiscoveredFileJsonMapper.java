package dev.codesoapbox.backity.core.files.discovery.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DiscoveredFileJsonMapper {

    DiscoveredFileJsonMapper INSTANCE = Mappers.getMapper(DiscoveredFileJsonMapper.class);

    @Mapping(source = "uniqueId", target = "id")
    @Mapping(source = "id.url", target = "url")
    @Mapping(source = "id.version", target = "version")
    DiscoveredFileJson toJson(DiscoveredFile domain);
}
