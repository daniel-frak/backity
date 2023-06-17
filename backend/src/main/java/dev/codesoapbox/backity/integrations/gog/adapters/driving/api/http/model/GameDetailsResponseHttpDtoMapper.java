package dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.model;

import dev.codesoapbox.backity.integrations.gog.domain.model.embed.GameDetailsResponse;
import org.mapstruct.Mapper;

@Mapper
public interface GameDetailsResponseHttpDtoMapper {

    GameDetailsResponseHttpDto toDto(GameDetailsResponse domain);
}
