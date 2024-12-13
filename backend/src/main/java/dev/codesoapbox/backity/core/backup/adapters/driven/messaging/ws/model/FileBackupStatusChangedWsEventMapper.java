package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFailedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFinishedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.gamefile.domain.FileBackupStatus;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import org.mapstruct.BeanMapping;
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

    @Mapping(target = "gameFileId", source = "gameFileId")
    @Mapping(target = "newStatus", expression = "java( statusInProgress() )")
    @Mapping(target = "failedReason", ignore = true)
    @BeanMapping(ignoreByDefault = true)
    public abstract FileBackupStatusChangedWsEvent toWsEvent(FileBackupStartedEvent event);

    protected String statusInProgress() {
        return FileBackupStatus.IN_PROGRESS.name();
    }
}
