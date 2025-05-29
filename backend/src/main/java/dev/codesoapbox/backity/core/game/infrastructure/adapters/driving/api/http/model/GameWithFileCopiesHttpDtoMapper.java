package dev.codesoapbox.backity.core.game.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.game.application.GameWithFileCopies;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.model.filecopy.FileCopyHttpDtoMapper;
import dev.codesoapbox.backity.core.game.infrastructure.adapters.driving.api.http.model.game.GameIdHttpDtoMapper;
import dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driving.api.http.model.gamefile.GameFileHttpDtoMapper;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {GameFileHttpDtoMapper.class, GameIdHttpDtoMapper.class, FileCopyHttpDtoMapper.class})
public interface GameWithFileCopiesHttpDtoMapper {

    @Mapping(target = ".", source = "game")
    @Mapping(target = "gameFilesWithCopies", source = "gameFilesWithCopies")
    @BeanMapping(ignoreUnmappedSourceProperties = "game")
    GameWithFileCopiesHttpDto toDto(GameWithFileCopies model);
}
