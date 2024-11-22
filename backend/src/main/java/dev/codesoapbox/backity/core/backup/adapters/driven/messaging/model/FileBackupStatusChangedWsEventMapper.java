package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class FileBackupStatusChangedWsEventMapper {

    @Mapping(target = "gameFileId", source = "id")
    @Mapping(target = ".", source = "fileBackup")
    @Mapping(target = "newStatus", source = "fileBackup.status")
    public abstract FileBackupStatusChangedWsEvent toWsEvent(GameFile domain);

    protected String toString(GameFileId id) {
        return id.value().toString();
    }
}
