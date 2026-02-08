package dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.sourcefile.domain.FileSize;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFileId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper
public abstract class SourceFileJpaEntityMapper {

    @Mapping(target = "sizeInBytes", source = "size")
    public abstract SourceFileJpaEntity toEntity(SourceFile model);

    protected UUID toUuid(GameId id) {
        return id.value();
    }

    protected UUID toUuid(SourceFileId id) {
        return id.value();
    }

    protected String getValue(GameProviderId gameProviderId) {
        return gameProviderId.value();
    }

    protected long getBytes(FileSize fileSize) {
        return fileSize.getBytes();
    }

    @Mapping(target = "size", source = "sizeInBytes")
    public abstract SourceFile toDomain(SourceFileJpaEntity entity);

    protected SourceFileId toSourceFileId(UUID uuid) {
        return new SourceFileId(uuid);
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
