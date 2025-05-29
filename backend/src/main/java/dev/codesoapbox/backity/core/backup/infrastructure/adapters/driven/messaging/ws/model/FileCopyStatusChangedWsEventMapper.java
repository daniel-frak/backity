package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFailedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFinishedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyNaturalId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class FileCopyStatusChangedWsEventMapper {

    @Mapping(target = "newStatus", expression = "java( statusStoredIntegrityUnknown() )")
    @Mapping(target = "failedReason", ignore = true)
    public abstract FileCopyStatusChangedWsEvent toWsEvent(FileBackupFinishedEvent event);

    protected String statusStoredIntegrityUnknown() {
        return FileCopyStatus.STORED_INTEGRITY_UNKNOWN.name();
    }

    protected String toString(FileCopyId id) {
        return id.value().toString();
    }

    protected String toString(GameFileId id) {
        return id.value().toString();
    }

    protected String toString(BackupTargetId id) {
        return id.value().toString();
    }

    @Mapping(target = "newStatus", expression = "java( statusFailed() )")
    public abstract FileCopyStatusChangedWsEvent toWsEvent(FileBackupFailedEvent event);

    protected String statusFailed() {
        return FileCopyStatus.FAILED.name();
    }

    @Mapping(target = "fileCopyId", source = "fileCopyId")
    @Mapping(target = "fileCopyNaturalId", source = "fileCopyNaturalId")
    @Mapping(target = "newStatus", expression = "java( statusInProgress() )")
    @Mapping(target = "failedReason", ignore = true)
    @BeanMapping(ignoreByDefault = true)
    public abstract FileCopyStatusChangedWsEvent toWsEvent(FileBackupStartedEvent event);

    protected abstract FileCopyNaturalIdWsDto toWsDto(FileCopyNaturalId fileCopyNaturalId);

    protected String statusInProgress() {
        return FileCopyStatus.IN_PROGRESS.name();
    }
}
