package dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.filecopy;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;

// @TODO Should this be in this package?
//       Maybe the "no sharing between adapters" rule can be more flexible for *.adapters.samepath.* packages?
@Mapper
public abstract class FileCopyHttpDtoMapper {

    @BeanMapping(ignoreUnmappedSourceProperties = "domainEvents")
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
