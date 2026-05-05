package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.gameproviders.gog.domain.GogGameWithFiles;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.SharedHttpDtoMapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = SharedHttpDtoMapperConfig.class)
public interface GogGameWithFilesHttpDtoMapper {

    GogGameWithFilesHttpDto toDto(GogGameWithFiles domain);
}
