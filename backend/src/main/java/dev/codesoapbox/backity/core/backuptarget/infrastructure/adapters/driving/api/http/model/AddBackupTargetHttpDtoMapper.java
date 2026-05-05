package dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.SharedHttpDtoMapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = SharedHttpDtoMapperConfig.class,
        uses = {
                BackupTargetHttpDtoMapper.class
        })
public interface AddBackupTargetHttpDtoMapper {

    AddBackupTargetHttpResponse toDto(BackupTarget backupTarget);
}
