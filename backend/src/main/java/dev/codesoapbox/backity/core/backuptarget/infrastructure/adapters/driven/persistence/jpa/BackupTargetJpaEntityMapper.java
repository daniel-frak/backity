package dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.persistence.jpa.filecopy.FileCopyValueObjectJpaDtoMapper;
import dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driven.persistence.jpa.SourceFileValueObjectJpaDtoMapper;
import dev.codesoapbox.backity.core.storagesolution.infrastructure.adapters.driven.persistence.jpa.StorageSolutionValueObjectJpaDtoMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.SharedJpaDtoMapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = SharedJpaDtoMapperConfig.class,
        uses = {
                FileCopyValueObjectJpaDtoMapper.class,
                BackupTargetValueObjectJpaDtoMapper.class,
                SourceFileValueObjectJpaDtoMapper.class,
                StorageSolutionValueObjectJpaDtoMapper.class,
        })
public abstract class BackupTargetJpaEntityMapper {

    public abstract BackupTargetJpaEntity toEntity(BackupTarget model);

    public abstract BackupTarget toDomain(BackupTargetJpaEntity entity);
}
