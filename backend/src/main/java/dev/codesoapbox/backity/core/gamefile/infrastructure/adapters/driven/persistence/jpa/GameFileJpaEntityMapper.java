package dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.gamefile.domain.FileSize;
import dev.codesoapbox.backity.core.gamefile.domain.FileSource;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.core.game.domain.GameId;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper
public abstract class GameFileJpaEntityMapper {

    @BeanMapping(ignoreUnmappedSourceProperties = "domainEvents")
    public abstract GameFileJpaEntity toEntity(GameFile model);

    @Mapping(target = "sizeInBytes", source = "size")
    protected abstract FileSourceJpaEntity toEntity(FileSource model);

    protected UUID toUuid(GameId id) {
        return id.value();
    }

    protected UUID toUuid(GameFileId id) {
        return id.value();
    }

    protected String getValue(GameProviderId gameProviderId) {
        return gameProviderId.value();
    }

    protected long getBytes(FileSize fileSize) {
        return fileSize.getBytes();
    }

    @Mapping(target = "domainEvents", expression = "java( new java.util.ArrayList<>() )")
    public abstract GameFile toModel(GameFileJpaEntity entity);

    @Mapping(target = "size", source = "sizeInBytes")
    protected abstract FileSource toModel(FileSourceJpaEntity entity);

    protected GameFileId toGameFileId(UUID uuid) {
        return new GameFileId(uuid);
    }

    protected GameId toGameId(UUID uuid) {
        return new GameId(uuid);
    }

    protected GameProviderId toFileGameProviderId(String value) {
        return new GameProviderId(value);
    }

    protected FileSize toFileSize(long sizeInBytes) {
        return new FileSize(sizeInBytes);
    }
}
