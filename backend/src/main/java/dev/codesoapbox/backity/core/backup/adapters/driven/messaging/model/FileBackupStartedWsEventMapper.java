package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class FileBackupStartedWsEventMapper {

    @Mapping(target = "gameFileId", source = "id")
    @Mapping(target = ".", source = "gameProviderFile")
    @Mapping(target = ".", source = "fileBackup")
    public abstract FileBackupStartedWsEvent toWsEvent(GameFile domain);

    protected String toString(GameFileId id) {
        return id.value().toString();
    }
}
