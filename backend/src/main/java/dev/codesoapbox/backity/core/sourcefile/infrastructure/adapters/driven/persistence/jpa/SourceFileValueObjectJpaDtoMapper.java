package dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.sourcefile.domain.FileSize;
import dev.codesoapbox.backity.core.sourcefile.domain.FileTitle;
import dev.codesoapbox.backity.core.sourcefile.domain.FileVersion;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFileId;

import java.util.UUID;

public class SourceFileValueObjectJpaDtoMapper {

    public String getValue(SourceFileId id) {
        return id.value().toString();
    }

    public SourceFileId toSourceFileId(UUID uuid) {
        return new SourceFileId(uuid);
    }

    public long getValue(FileSize fileSize) {
        return fileSize.getBytes();
    }

    public FileSize toFileSize(long sizeInBytes) {
        return new FileSize(sizeInBytes);
    }

    public String getValue(FileTitle fileTitle) {
        return fileTitle.value();
    }

    public FileTitle toFileTitle(String value) {
        return new FileTitle(value);
    }

    public String getValue(FileVersion fileVersion) {
        return fileVersion.value();
    }

    public FileVersion toFileVersion(String value) {
        return new FileVersion(value);
    }
}
