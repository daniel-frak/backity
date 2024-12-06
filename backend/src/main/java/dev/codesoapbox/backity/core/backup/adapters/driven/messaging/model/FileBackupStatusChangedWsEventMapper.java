package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFailedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFinishedEvent;
import dev.codesoapbox.backity.core.gamefile.domain.FileBackupStatus;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class FileBackupStatusChangedWsEventMapper {

    @Mapping(target = "newStatus", expression = "java( statusSuccess() )")
    @Mapping(target = "failedReason", ignore = true)
    public abstract FileBackupStatusChangedWsEvent toWsEvent(FileBackupFinishedEvent event);

    protected String statusSuccess() {
        return FileBackupStatus.SUCCESS.name();
    }

    protected String toString(GameFileId id) {
        return id.value().toString();
    }

    @Mapping(target = "newStatus", expression = "java( statusFailed() )")
    public abstract FileBackupStatusChangedWsEvent toWsEvent(FileBackupFailedEvent event);

    protected String statusFailed() {
        return FileBackupStatus.FAILED.name();
    }

}
