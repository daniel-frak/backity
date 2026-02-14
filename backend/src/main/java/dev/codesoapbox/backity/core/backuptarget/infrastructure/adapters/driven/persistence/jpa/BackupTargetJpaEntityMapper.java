package dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.backuptarget.domain.PathTemplate;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper
public abstract class BackupTargetJpaEntityMapper {

    public abstract BackupTargetJpaEntity toEntity(BackupTarget model);

    public abstract BackupTarget toDomain(BackupTargetJpaEntity entity);

    protected UUID getValue(BackupTargetId id) {
        return id.value();
    }

    protected String getValue(PathTemplate pathTemplate) {
        return pathTemplate.value();
    }

    protected BackupTargetId toBackupTargetId(UUID value) {
        return new BackupTargetId(value);
    }

    protected String getValue(StorageSolutionId id) {
        return id.value();
    }

    protected StorageSolutionId toStorageSolutionId(String value) {
        return new StorageSolutionId(value);
    }

    protected PathTemplate toPathTemplate(String value) {
        return new PathTemplate(value);
    }
}
