package dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.sourcefile.domain.*;

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

    public String getValue(SourceFileUrl sourceFileUrl) {
        return sourceFileUrl.value();
    }

    public SourceFileUrl toSourceFileUrl(String value) {
        return new SourceFileUrl(value);
    }

    public String getValue(FileName fileName) {
        return fileName.value();
    }

    public FileName toFileName(String value) {
        return new FileName(value);
    }
}
