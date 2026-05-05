package dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driven.messaging.ws.model.sourcefile;

import dev.codesoapbox.backity.core.sourcefile.domain.FileSize;
import dev.codesoapbox.backity.core.sourcefile.domain.FileTitle;
import dev.codesoapbox.backity.core.sourcefile.domain.FileVersion;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFileId;

public class SourceFileValueObjectWsDtoMapper {

    public String getValue(FileSize fileSize) {
        return fileSize.toString();
    }

    public String getValue(FileTitle fileTitle) {
        return fileTitle.value();
    }

    public String getValue(FileVersion fileVersion) {
        return fileVersion.value();
    }

    public String getValue(SourceFileId id) {
        return id.value().toString();
    }
}
