package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetails;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetailsId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class FileBackupStartedMessageMapper {

    @Mapping(target = "gameFileDetailsId", source = "id")
    @Mapping(target = ".", source = "sourceFileDetails")
    @Mapping(target = ".", source = "backupDetails")
    public abstract FileBackupStartedMessage toMessage(GameFileDetails domain);

    protected String toString(GameFileDetailsId id) {
        return id.value().toString();
    }
}
