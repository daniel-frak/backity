package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driven.persistence.jpa.BackupTargetValueObjectJpaDtoMapper;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.persistence.jpa.filecopy.FileCopyValueObjectJpaDtoMapper;
import dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driven.persistence.jpa.SourceFileValueObjectJpaDtoMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.SharedJpaDtoMapperConfig;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = SharedJpaDtoMapperConfig.class,
        uses = {
                FileCopyValueObjectJpaDtoMapper.class,
                BackupTargetValueObjectJpaDtoMapper.class,
                SourceFileValueObjectJpaDtoMapper.class
        })
public abstract class FileCopyJpaEntityMapper {

    @BeanMapping(ignoreUnmappedSourceProperties = {"domainEvents", "stored"})
    public abstract FileCopyJpaEntity toEntity(FileCopy model);

    @Mapping(target = "domainEvents", ignore = true)
    public abstract FileCopy toDomain(FileCopyJpaEntity entity);
}
