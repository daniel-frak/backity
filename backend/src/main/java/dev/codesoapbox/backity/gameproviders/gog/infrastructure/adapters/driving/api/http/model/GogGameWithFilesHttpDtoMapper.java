package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.gameproviders.gog.domain.GogGameWithFiles;
import org.mapstruct.Mapper;

@Mapper
public interface GogGameWithFilesHttpDtoMapper {

    GogGameWithFilesHttpDto toDto(GogGameWithFiles domain);
}
