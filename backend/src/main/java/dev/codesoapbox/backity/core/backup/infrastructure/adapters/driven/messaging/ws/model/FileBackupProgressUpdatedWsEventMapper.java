package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupProgressChangedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface FileBackupProgressUpdatedWsEventMapper {

    FileBackupProgressUpdatedWsEvent toWsEvent(FileBackupProgressChangedEvent domain);
}
