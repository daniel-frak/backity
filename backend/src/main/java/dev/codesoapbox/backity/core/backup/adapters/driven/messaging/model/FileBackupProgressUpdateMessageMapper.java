package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.backup.domain.FileBackupProgress;
import org.mapstruct.Mapper;

@Mapper
public abstract class FileBackupProgressUpdateMessageMapper {

    public abstract FileBackupProgressUpdateWsMessage toMessage(FileBackupProgress domain);
}
