package dev.codesoapbox.backity.core.files.adapters.driven.persistence;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetailsId;
import dev.codesoapbox.backity.core.files.domain.game.GameId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper
public abstract class GameFileDetailsJpaEntityMapper {

    @Mapping(target = "game.id", source = "gameId")
    public abstract GameFileDetailsJpaEntity toEntity(GameFileDetails model);

    @Mapping(target = "gameId", source = "game.id")
    public abstract GameFileDetails toModel(GameFileDetailsJpaEntity entity);

    protected UUID toUuid(GameId id) {
        return id.value();
    }

    protected UUID toUuid(GameFileDetailsId id) {
        return id.value();
    }

    protected GameFileDetailsId toGameFileDetailsId(UUID uuid) {
        return new GameFileDetailsId(uuid);
    }

    protected GameId toGameId(UUID uuid) {
        return new GameId(uuid);
    }
}
