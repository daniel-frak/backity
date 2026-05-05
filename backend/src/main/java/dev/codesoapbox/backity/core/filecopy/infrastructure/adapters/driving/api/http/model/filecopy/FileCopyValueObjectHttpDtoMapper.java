package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.model.filecopy;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyFailureReason;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.storagesolution.domain.FilePath;

public class FileCopyValueObjectHttpDtoMapper {

    protected String getValue(FileCopyId id) {
        return id.value().toString();
    }

    protected String getValue(FilePath filePath) {
        return filePath.toString();
    }

    protected String getValue(FileCopyFailureReason reason) {
        return reason.value();
    }
}
