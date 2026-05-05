package dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetName;
import dev.codesoapbox.backity.core.backuptarget.domain.PathTemplate;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public abstract class BackupTargetHttpDtoMapper {

    public abstract BackupTargetHttpDto toDto(BackupTarget backupTarget);

    protected String getValue(BackupTargetId id) {
        return id.value().toString();
    }

    protected String getValue(BackupTargetName name) {
        return name.value();
    }

    protected String getValue(StorageSolutionId id) {
        return id.value();
    }

    protected String getValue(PathTemplate pathTemplate) {
        return pathTemplate.value();
    }
}
