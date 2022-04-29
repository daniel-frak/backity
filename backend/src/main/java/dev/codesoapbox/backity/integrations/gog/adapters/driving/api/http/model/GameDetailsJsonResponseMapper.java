package dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.model;

import dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.model.embed.GameDetailsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GameDetailsJsonResponseMapper {

    GameDetailsJsonResponseMapper INSTANCE = Mappers.getMapper(GameDetailsJsonResponseMapper.class);

    GameDetailsJsonResponse toJson(GameDetailsResponse domain);
}
