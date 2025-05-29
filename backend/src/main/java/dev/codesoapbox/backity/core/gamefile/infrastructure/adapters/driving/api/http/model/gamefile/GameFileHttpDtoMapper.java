package dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driving.api.http.model.gamefile;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.gamefile.domain.FileSize;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.core.game.infrastructure.adapters.driving.api.http.model.game.GameIdHttpDtoMapper;
import org.mapstruct.Mapper;

@Mapper(uses = GameIdHttpDtoMapper.class)
public abstract class GameFileHttpDtoMapper {

    public abstract GameFileHttpDto toDto(GameFile domain);

    protected String toString(GameFileId id) {
        return id.value().toString();
    }

    protected String toString(GameProviderId gameProviderId) {
        return gameProviderId.value();
    }

    protected String toString(FileSize fileSize) {
        return fileSize.toString();
    }
}
