package dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driving.api.http.model.sourcefile;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.game.infrastructure.adapters.driving.api.http.model.game.GameIdHttpDtoMapper;
import dev.codesoapbox.backity.core.sourcefile.domain.FileSize;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFileId;
import org.mapstruct.Mapper;

@Mapper(uses = GameIdHttpDtoMapper.class)
public abstract class SourceFileHttpDtoMapper {

    public abstract SourceFileHttpDto toDto(SourceFile domain);

    protected String toString(SourceFileId id) {
        return id.value().toString();
    }

    protected String toString(GameProviderId gameProviderId) {
        return gameProviderId.value();
    }

    protected String toString(FileSize fileSize) {
        return fileSize.toString();
    }
}
