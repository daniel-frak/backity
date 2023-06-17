package dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.model;

import dev.codesoapbox.backity.integrations.gog.domain.model.embed.GameDetailsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GameDetailsResponseHttpDtoMapper {

    GameDetailsResponseHttpDtoMapper INSTANCE = Mappers.getMapper(GameDetailsResponseHttpDtoMapper.class);

    GameDetailsResponseHttpDto toDto(GameDetailsResponse domain);
}
