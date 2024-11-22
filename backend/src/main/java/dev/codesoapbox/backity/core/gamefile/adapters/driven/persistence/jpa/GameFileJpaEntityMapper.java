package dev.codesoapbox.backity.core.gamefile.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.core.game.domain.GameId;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper
public abstract class GameFileJpaEntityMapper {

    public abstract GameFileJpaEntity toEntity(GameFile model);

    public abstract GameFile toModel(GameFileJpaEntity entity);

    protected UUID toUuid(GameId id) {
        return id.value();
    }

    protected UUID toUuid(GameFileId id) {
        return id.value();
    }

    protected String getValue(GameProviderId gameProviderId) {
        return gameProviderId.value();
    }

    protected GameFileId toGameFileId(UUID uuid) {
        return new GameFileId(uuid);
    }

    protected GameId toGameId(UUID uuid) {
        return new GameId(uuid);
    }

    protected GameProviderId toFileGameProviderId(String value) {
        return new GameProviderId(value);
    }
}
