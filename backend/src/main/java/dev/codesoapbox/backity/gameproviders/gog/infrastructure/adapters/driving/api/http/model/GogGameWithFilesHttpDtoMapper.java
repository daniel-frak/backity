package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.gameproviders.gog.domain.GogGameWithFiles;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface GogGameWithFilesHttpDtoMapper {

    GogGameWithFilesHttpDto toDto(GogGameWithFiles domain);
}
