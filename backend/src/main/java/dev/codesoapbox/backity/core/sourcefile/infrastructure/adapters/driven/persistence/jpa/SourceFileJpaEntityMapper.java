package dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.persistence.jpa.BackupValueObjectJpaDtoMapper;
import dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driven.persistence.jpa.BackupTargetValueObjectJpaDtoMapper;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.persistence.jpa.filecopy.FileCopyValueObjectJpaDtoMapper;
import dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.GameValueObjectJpaDtoMapper;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.SharedJpaDtoMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = SharedJpaDtoMapperConfig.class,
        uses = {
                FileCopyValueObjectJpaDtoMapper.class,
                BackupTargetValueObjectJpaDtoMapper.class,
                SourceFileValueObjectJpaDtoMapper.class,
                GameValueObjectJpaDtoMapper.class,
                BackupValueObjectJpaDtoMapper.class,
        })
public abstract class SourceFileJpaEntityMapper {

    @Mapping(target = "sizeInBytes", source = "size")
    public abstract SourceFileJpaEntity toEntity(SourceFile model);

    @Mapping(target = "size", source = "sizeInBytes")
    public abstract SourceFile toDomain(SourceFileJpaEntity entity);
}
