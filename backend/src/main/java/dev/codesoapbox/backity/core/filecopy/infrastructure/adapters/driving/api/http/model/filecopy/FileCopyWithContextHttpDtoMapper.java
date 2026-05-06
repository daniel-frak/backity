package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.model.filecopy;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgress;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.api.http.model.BackupValueObjectHttpDtoMapper;
import dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driving.api.http.model.BackupTargetValueObjectHttpDtoMapper;
import dev.codesoapbox.backity.core.filecopy.application.usecases.FileCopyWithContext;
import dev.codesoapbox.backity.core.game.infrastructure.adapters.driving.api.http.model.game.GameValueObjectHttpDtoMapper;
import dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driving.api.http.model.sourcefile.SourceFileValueObjectHttpDtoMapper;
import dev.codesoapbox.backity.core.storagesolution.infrastructure.adapters.driving.api.http.model.StorageSolutionValueObjectHttpDtoMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.SharedHttpDtoMapperConfig;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.ProgressHttpDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = SharedHttpDtoMapperConfig.class,
        uses = {
                GameValueObjectHttpDtoMapper.class,
                FileCopyValueObjectHttpDtoMapper.class,
                BackupTargetValueObjectHttpDtoMapper.class,
                SourceFileValueObjectHttpDtoMapper.class,
                BackupValueObjectHttpDtoMapper.class,
                StorageSolutionValueObjectHttpDtoMapper.class
        })
public interface FileCopyWithContextHttpDtoMapper {

    FileCopyWithContextHttpDto toDto(FileCopyWithContext fileCopyWithContext);

    @Mapping(target = "timeLeftSeconds", source = "timeLeft.seconds")
    ProgressHttpDto toDto(FileCopyReplicationProgress progress);
}
