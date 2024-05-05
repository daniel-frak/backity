package dev.codesoapbox.backity.core.filedetails.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.backup.domain.FileSourceId;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetailsId;
import dev.codesoapbox.backity.core.game.domain.GameId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper
public abstract class FileDetailsJpaEntityMapper {

    @Mapping(target = "game.id", source = "gameId")
    public abstract FileDetailsJpaEntity toEntity(FileDetails model);

    @Mapping(target = "gameId", source = "game.id")
    public abstract FileDetails toModel(FileDetailsJpaEntity entity);

    protected UUID toUuid(GameId id) {
        return id.value();
    }

    protected UUID toUuid(FileDetailsId id) {
        return id.value();
    }

    protected String getValue(FileSourceId fileSourceId) {
        return fileSourceId.value();
    }

    protected FileDetailsId toFileDetailsId(UUID uuid) {
        return new FileDetailsId(uuid);
    }

    protected GameId toGameId(UUID uuid) {
        return new GameId(uuid);
    }

    protected FileSourceId toFileSourceId(String value) {
        return new FileSourceId(value);
    }
}
