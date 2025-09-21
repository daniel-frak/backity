package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.backup.domain.events.FileCopyReplicationProgressChangedEvent;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class FileCopyReplicationProgressUpdatedWsEventMapper {

    @Mapping(target = "timeLeftSeconds", source = "timeLeft.seconds")
    public abstract FileCopyReplicationProgressUpdatedWsEvent toWsEvent(FileCopyReplicationProgressChangedEvent domain);

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
