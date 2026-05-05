package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyFailureReason;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFileId;
import dev.codesoapbox.backity.core.storagesolution.domain.FilePath;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public abstract class FileCopyJpaEntityMapper {

    @BeanMapping(ignoreUnmappedSourceProperties = {"domainEvents", "stored"})
    public abstract FileCopyJpaEntity toEntity(FileCopy model);

    protected UUID getValue(FileCopyId id) {
        return id.value();
    }

    protected UUID getValue(SourceFileId id) {
        return id.value();
    }

    protected UUID getValue(BackupTargetId id) {
        return id.value();
    }

    protected String getValue(FilePath path) {
        return path.toString();
    }

    protected String getValue(FileCopyFailureReason reason) {
        return reason.value();
    }

    @Mapping(target = "domainEvents", ignore = true)
    public abstract FileCopy toDomain(FileCopyJpaEntity entity);

    protected FileCopyId toFileCopyId(UUID uuid) {
        return new FileCopyId(uuid);
    }

    protected SourceFileId toSourceFileId(UUID uuid) {
        return new SourceFileId(uuid);
    }

    protected BackupTargetId toBackupTargetId(UUID uuid) {
        return new BackupTargetId(uuid);
    }

    protected FilePath toFilePath(String value) {
        return new FilePath(value);
    }

    protected FileCopyFailureReason toFileCopyFailureReason(String reason) {
        return new FileCopyFailureReason(reason);
    }
}
