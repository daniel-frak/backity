package dev.codesoapbox.backity.core.files.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GameFileDetailsJsonMapper {

    GameFileDetailsJsonMapper INSTANCE = Mappers.getMapper(GameFileDetailsJsonMapper.class);

    GameFileDetailsJson toJson(GameFileDetails domain);
}
