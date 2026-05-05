package dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driving.api.http.model.sourcefile;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.game.domain.GameTitle;
import dev.codesoapbox.backity.core.game.infrastructure.adapters.driving.api.http.model.game.GameIdHttpDtoMapper;
import dev.codesoapbox.backity.core.sourcefile.domain.FileSize;
import dev.codesoapbox.backity.core.sourcefile.domain.FileTitle;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFileId;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = GameIdHttpDtoMapper.class)
public abstract class SourceFileHttpDtoMapper {

    public abstract SourceFileHttpDto toDto(SourceFile domain);

    protected String getValue(SourceFileId id) {
        return id.value().toString();
    }

    protected String getValue(GameProviderId gameProviderId) {
        return gameProviderId.value();
    }

    protected String getValue(FileSize fileSize) {
        return fileSize.toString();
    }

    protected String getValue(GameTitle gameTitle) {
        return gameTitle.value();
    }

    protected String getValue(FileTitle fileTitle) {
        return fileTitle.value();
    }
}
