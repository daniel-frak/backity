package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetailsId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class FileBackupStartedMessageMapper {

    @Mapping(target = "fileDetailsId", source = "id")
    @Mapping(target = ".", source = "sourceFileDetails")
    @Mapping(target = ".", source = "backupDetails")
    public abstract FileBackupStartedWsMessage toMessage(FileDetails domain);

    protected String toString(FileDetailsId id) {
        return id.value().toString();
    }
}
