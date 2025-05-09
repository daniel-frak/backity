package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.gamefile.domain.FileSize;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class FileBackupStartedWsEventMapper {

    public abstract FileBackupStartedWsEvent toWsEvent(FileBackupStartedEvent event);

    protected String toString(GameFileId id) {
        return id.value().toString();
    }

    protected String toString(FileSize fileSize) {
        return fileSize.toString();
    }
}
