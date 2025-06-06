package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.backup.domain.events.FileDownloadProgressChangedEvent;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class FileDownloadProgressUpdatedWsEventMapper {

    @Mapping(target = "timeLeftSeconds", source = "timeLeft.seconds")
    public abstract FileDownloadProgressUpdatedWsEvent toWsEvent(FileDownloadProgressChangedEvent domain);

    protected String getValue(FileCopyId id) {
        return id.value().toString();
    }

    protected String toString(GameFileId id) {
        return id.value().toString();
    }

    protected String toString(BackupTargetId id) {
        return id.value().toString();
    }
}
