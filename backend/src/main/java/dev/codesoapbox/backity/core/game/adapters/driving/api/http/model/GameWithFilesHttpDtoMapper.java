package dev.codesoapbox.backity.core.game.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.game.application.GameWithFiles;
import dev.codesoapbox.backity.shared.adapters.driving.api.http.model.gamefile.GameFileHttpDtoMapper;
import dev.codesoapbox.backity.shared.adapters.driving.api.http.model.game.GameIdHttpDtoMapper;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {GameFileHttpDtoMapper.class, GameIdHttpDtoMapper.class})
public interface GameWithFilesHttpDtoMapper {

    @Mapping(target = ".", source = "game")
    @BeanMapping(ignoreUnmappedSourceProperties = "game")
    GameWithFilesHttpDto toDto(GameWithFiles model);
}
