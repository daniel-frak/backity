package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.gamefile.domain.FileSize;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class FileBackupStartedWsEventMapper {

    @Mapping(target = ".", source = "gameFile.fileSource")
    public abstract FileBackupStartedWsEvent toWsEvent(FileBackupStartedEvent event, GameFile gameFile);

    protected String toString(FileCopyId id) {
        return id.value().toString();
    }

    protected String toString(GameFileId id) {
        return id.value().toString();
    }

    protected String toString(BackupTargetId id) {
        return id.value().toString();
    }

    protected String toString(FileSize fileSize) {
        return fileSize.toString();
    }
}
