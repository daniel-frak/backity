package dev.codesoapbox.backity.core.files.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersion;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GameFileVersionJsonMapper {

    GameFileVersionJsonMapper INSTANCE = Mappers.getMapper(GameFileVersionJsonMapper.class);

    GameFileVersionJson toJson(GameFileVersion domain);
}
