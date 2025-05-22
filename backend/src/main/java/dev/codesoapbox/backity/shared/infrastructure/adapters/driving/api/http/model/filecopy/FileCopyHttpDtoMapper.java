package dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.filecopy;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import org.mapstruct.Mapper;

// @TODO Should this be in this package?
@Mapper
public abstract class FileCopyHttpDtoMapper {

    public abstract FileCopyHttpDto toDto(FileCopy fileCopy);

    protected String getValue(FileCopyId id) {
        return id.value().toString();
    }

    protected String getValue(GameFileId id) {
        return id.value().toString();
    }

    protected String getValue(BackupTargetId id) {
        return id.value().toString();
    }
}
