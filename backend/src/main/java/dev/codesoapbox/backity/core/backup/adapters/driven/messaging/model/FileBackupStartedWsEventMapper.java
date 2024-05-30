package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetailsId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class FileBackupStartedWsEventMapper {

    @Mapping(target = "fileDetailsId", source = "id")
    @Mapping(target = ".", source = "sourceFileDetails")
    @Mapping(target = ".", source = "backupDetails")
    public abstract FileBackupStartedWsEvent toWsEvent(FileDetails domain);

    protected String toString(FileDetailsId id) {
        return id.value().toString();
    }
}
