package dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.filedetails;

import dev.codesoapbox.backity.core.backup.domain.FileSourceId;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetailsId;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.game.GameIdHttpDtoMapper;
import org.mapstruct.Mapper;

@Mapper(uses = GameIdHttpDtoMapper.class)
public abstract class FileDetailsHttpDtoMapper {

    public abstract FileDetailsHttpDto toDto(FileDetails domain);

    protected String toString(FileDetailsId id) {
        return id.value().toString();
    }

    protected String toString(FileSourceId fileSourceId) {
        return fileSourceId.value();
    }
}
