package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.model.filecopy;

import dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driving.api.http.model.BackupTargetValueObjectHttpDtoMapper;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driving.api.http.model.sourcefile.SourceFileValueObjectHttpDtoMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.SharedHttpDtoMapperConfig;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;

@Mapper(config = SharedHttpDtoMapperConfig.class,
        uses = {
                FileCopyValueObjectHttpDtoMapper.class,
                SourceFileValueObjectHttpDtoMapper.class,
                BackupTargetValueObjectHttpDtoMapper.class
        })
public interface FileCopyHttpDtoMapper {

    @BeanMapping(ignoreUnmappedSourceProperties = {"domainEvents", "stored"})
    FileCopyHttpDto toDto(FileCopy fileCopy);
}
