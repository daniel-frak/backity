package dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driven.messaging.ws.model.sourcefile;

import dev.codesoapbox.backity.core.sourcefile.domain.SourceFileId;

public class SourceFileValueObjectWsDtoMapper {

    public String getValue(SourceFileId id) {
        return id.value().toString();
    }
}
