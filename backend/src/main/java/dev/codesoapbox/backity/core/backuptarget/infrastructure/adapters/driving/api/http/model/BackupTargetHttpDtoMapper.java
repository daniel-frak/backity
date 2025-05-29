package dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class BackupTargetHttpDtoMapper {

    public abstract BackupTargetHttpDto toDto(BackupTarget backupTarget);

    protected String getValue(BackupTargetId id) {
        return id.value().toString();
    }

    protected String getValue(StorageSolutionId id) {
        return id.value();
    }
}
