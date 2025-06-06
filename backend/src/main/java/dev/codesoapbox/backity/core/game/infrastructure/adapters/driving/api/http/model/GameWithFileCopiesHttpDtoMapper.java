package dev.codesoapbox.backity.core.game.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgress;
import dev.codesoapbox.backity.core.game.application.GameWithFileCopies;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.model.filecopy.FileCopyHttpDtoMapper;
import dev.codesoapbox.backity.core.game.infrastructure.adapters.driving.api.http.model.game.GameIdHttpDtoMapper;
import dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driving.api.http.model.gamefile.GameFileHttpDtoMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.ProgressHttpDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE,
        uses = {GameFileHttpDtoMapper.class, GameIdHttpDtoMapper.class, FileCopyHttpDtoMapper.class})
public abstract class GameWithFileCopiesHttpDtoMapper {

    @Mapping(target = ".", source = "game")
    @Mapping(target = "gameFilesWithCopies", source = "gameFilesWithCopies")
    @BeanMapping(ignoreUnmappedSourceProperties = "game")
    public abstract GameWithFileCopiesHttpDto toDto(GameWithFileCopies model);

    @Mapping(target = "timeLeftSeconds", source = "timeLeft.seconds")
    protected abstract ProgressHttpDto toDto(FileCopyReplicationProgress domain);
}
