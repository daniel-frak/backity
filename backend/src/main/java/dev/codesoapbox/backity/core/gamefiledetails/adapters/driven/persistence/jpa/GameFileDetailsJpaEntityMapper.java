package dev.codesoapbox.backity.core.gamefiledetails.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.backup.domain.FileSourceId;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetails;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetailsId;
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

    protected String getValue(FileSourceId fileSourceId) {
        return fileSourceId.value();
    }

    protected GameFileDetailsId toGameFileDetailsId(UUID uuid) {
        return new GameFileDetailsId(uuid);
    }

    protected GameId toGameId(UUID uuid) {
        return new GameId(uuid);
    }

    protected FileSourceId toFileSourceId(String value) {
        return new FileSourceId(value);
    }
}
