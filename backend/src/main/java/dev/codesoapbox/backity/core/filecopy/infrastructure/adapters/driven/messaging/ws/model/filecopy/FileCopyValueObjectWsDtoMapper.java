package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.messaging.ws.model.filecopy;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyFailureReason;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.storagesolution.domain.FilePath;

public class FileCopyValueObjectWsDtoMapper {

    public String getValue(FileCopyId id) {
        return id.value().toString();
    }

    public String getValue(FilePath filePath) {
        return filePath.toString();
    }

    public String getValue(FileCopyFailureReason reason) {
        return reason.value();
    }
}
