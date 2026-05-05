package dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driving.api.http.model.sourcefile;

import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.api.http.model.BackupValueObjectHttpDtoMapper;
import dev.codesoapbox.backity.core.game.infrastructure.adapters.driving.api.http.model.game.GameValueObjectHttpDtoMapper;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.SharedHttpDtoMapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = SharedHttpDtoMapperConfig.class,
        uses = {
                SourceFileValueObjectHttpDtoMapper.class,
                GameValueObjectHttpDtoMapper.class,
                BackupValueObjectHttpDtoMapper.class
        })
public abstract class SourceFileHttpDtoMapper {

    public abstract SourceFileHttpDto toDto(SourceFile domain);

}
