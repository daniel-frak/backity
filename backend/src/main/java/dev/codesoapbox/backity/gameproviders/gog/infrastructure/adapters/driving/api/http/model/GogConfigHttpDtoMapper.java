package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.gameproviders.gog.application.GogConfigInfo;
import org.mapstruct.Mapper;

@Mapper
public interface GogConfigHttpDtoMapper {

    GogConfigHttpDto toDto(GogConfigInfo domain);
}
