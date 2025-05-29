package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.filecopy.application.usecases.FileCopyWithContext;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.gamefile.domain.FileSize;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class FileBackupStartedWsEventMapper {

    public abstract FileBackupStartedWsEvent toWsEvent(FileBackupStartedEvent event,
                                                       FileCopyWithContext fileCopyWithContext);

    protected String getValue(FileCopyId id) {
        return id.value().toString();
    }

    protected String getValue(GameFileId id) {
        return id.value().toString();
    }

    protected String getValue(BackupTargetId id) {
        return id.value().toString();
    }

    protected String getValue(GameProviderId id) {
        return id.value();
    }

    protected String getValue(FileSize fileSize) {
        return fileSize.toString();
    }
}
