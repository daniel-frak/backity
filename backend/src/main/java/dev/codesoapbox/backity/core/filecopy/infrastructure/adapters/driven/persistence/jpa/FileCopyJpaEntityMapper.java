package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper
public abstract class FileCopyJpaEntityMapper {

    @BeanMapping(ignoreUnmappedSourceProperties = "domainEvents")
    public abstract FileCopyJpaEntity toEntity(FileCopy model);

    protected UUID toUuid(FileCopyId id) {
        return id.value();
    }

    protected UUID toUuid(GameFileId id) {
        return id.value();
    }

    protected UUID toUuid(BackupTargetId id) {
        return id.value();
    }

    @Mapping(target = "domainEvents", ignore = true)
    public abstract FileCopy toDomain(FileCopyJpaEntity entity);

    protected FileCopyId toFileCopyId(UUID uuid) {
        return new FileCopyId(uuid);
    }

    protected GameFileId toGameFileId(UUID uuid) {
        return new GameFileId(uuid);
    }

    protected BackupTargetId toBackupTargetId(UUID uuid) {
        return new BackupTargetId(uuid);
    }
}
