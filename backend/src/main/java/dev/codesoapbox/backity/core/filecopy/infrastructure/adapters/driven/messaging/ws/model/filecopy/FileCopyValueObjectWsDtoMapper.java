package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.messaging.ws.model.filecopy;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyFailureReason;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;

public class FileCopyValueObjectWsDtoMapper {

    public String getValue(FileCopyId id) {
        return id.value().toString();
    }

    public String getValue(FileCopyFailureReason reason) {
        return reason.value();
    }
}
