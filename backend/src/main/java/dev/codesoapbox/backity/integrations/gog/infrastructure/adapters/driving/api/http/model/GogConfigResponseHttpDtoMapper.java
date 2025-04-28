package dev.codesoapbox.backity.integrations.gog.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.integrations.gog.application.GogConfigInfo;
import org.mapstruct.Mapper;

@Mapper
public interface GogConfigResponseHttpDtoMapper {

    GogConfigResponseHttpDto toDto(GogConfigInfo domain);
}
