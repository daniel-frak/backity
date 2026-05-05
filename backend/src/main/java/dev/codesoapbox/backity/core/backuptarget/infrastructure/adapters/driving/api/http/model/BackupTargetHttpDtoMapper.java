package dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.storagesolution.infrastructure.adapters.driving.api.http.model.StorageSolutionValueObjectHttpDtoMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.SharedHttpDtoMapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = SharedHttpDtoMapperConfig.class,
        uses = {
                BackupTargetValueObjectHttpDtoMapper.class,
                StorageSolutionValueObjectHttpDtoMapper.class
        })
public abstract class BackupTargetHttpDtoMapper {

    public abstract BackupTargetHttpDto toDto(BackupTarget backupTarget);

}
