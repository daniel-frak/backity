package dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE, uses = BackupTargetHttpDtoMapper.class)
public interface AddBackupTargetHttpDtoMapper {

    AddBackupTargetHttpResponse toDto(BackupTarget backupTarget);
}
