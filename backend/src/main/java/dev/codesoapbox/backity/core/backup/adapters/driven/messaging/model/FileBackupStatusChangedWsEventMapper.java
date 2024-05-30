package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetailsId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class FileBackupStatusChangedWsEventMapper {

    @Mapping(target = "fileDetailsId", source = "id")
    @Mapping(target = ".", source = "backupDetails")
    @Mapping(target = "newStatus", source = "backupDetails.status")
    public abstract FileBackupStatusChangedWsEvent toWsEvent(FileDetails domain);

    protected String toString(FileDetailsId id) {
        return id.value().toString();
    }
}
