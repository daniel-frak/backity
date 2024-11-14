package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.backup.domain.FileBackupProgress;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface FileBackupProgressUpdatedWsEventMapper {

    FileBackupProgressUpdatedWsEvent toWsEvent(FileBackupProgress domain);
}
