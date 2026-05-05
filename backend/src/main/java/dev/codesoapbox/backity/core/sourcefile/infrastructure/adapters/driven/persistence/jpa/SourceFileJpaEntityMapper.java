package dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.game.domain.GameTitle;
import dev.codesoapbox.backity.core.sourcefile.domain.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public abstract class SourceFileJpaEntityMapper {

    @Mapping(target = "sizeInBytes", source = "size")
    public abstract SourceFileJpaEntity toEntity(SourceFile model);

    protected UUID getValue(GameId id) {
        return id.value();
    }

    protected UUID getValue(SourceFileId id) {
        return id.value();
    }

    protected String getValue(GameTitle gameTitle) {
        return gameTitle.value();
    }

    protected String getValue(FileTitle fileTitle) {
        return fileTitle.value();
    }

    protected String getValue(GameProviderId gameProviderId) {
        return gameProviderId.value();
    }

    protected String getValue(FileVersion fileVersion) {
        return fileVersion.value();
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

    protected GameTitle toGameTitle(String value) {
        return new GameTitle(value);
    }

    protected FileTitle toFileTitle(String value) {
        return new FileTitle(value);
    }

    protected GameProviderId toFileGameProviderId(String value) {
        return new GameProviderId(value);
    }

    protected FileSize toFileSize(long sizeInBytes) {
        return new FileSize(sizeInBytes);
    }

    protected FileVersion toFileVersion(String value) {
        return new FileVersion(value);
    }
}
