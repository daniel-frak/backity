package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.gameproviders.gog.application.GogConfigInfo;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.SharedHttpDtoMapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = SharedHttpDtoMapperConfig.class)
public interface GogConfigHttpDtoMapper {

    GogConfigHttpDto toDto(GogConfigInfo domain);
}
