package dev.codesoapbox.backity.core.files.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.files.application.GameWithFiles;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {GameFileVersionJsonMapper.class, GameIdJsonMapper.class})
public abstract class GameWithFilesJsonMapper {

    @Mapping(target = ".", source = "game")
    @BeanMapping(ignoreUnmappedSourceProperties = "game")
    public abstract GameWithFilesJson toDto(GameWithFiles model);
}
