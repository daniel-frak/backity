package dev.codesoapbox.backity.core.game.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.game.application.GameWithFileCopies;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.gamefile.GameFileHttpDtoMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.game.GameIdHttpDtoMapper;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {GameFileHttpDtoMapper.class, GameIdHttpDtoMapper.class})
public abstract class GameWithFileCopiesHttpDtoMapper {

    @Mapping(target = ".", source = "game")
    @BeanMapping(ignoreUnmappedSourceProperties = "game")
    public abstract GameWithFileCopiesHttpDto toDto(GameWithFileCopies model);

    protected String getValue(FileCopyId id) {
        return id.value().toString();
    }

    protected String getValue(GameFileId id) {
        return id.value().toString();
    }

    protected String getValue(BackupTargetId id) {
        return id.value().toString();
    }
}
