package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.persistence.jpa.filecopy;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyFailureReason;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.storagesolution.domain.FilePath;

import java.util.UUID;

public class FileCopyValueObjectJpaDtoMapper {

    public String getValue(FileCopyId id) {
        return id.value().toString();
    }

    public FileCopyId toFileCopyId(UUID uuid) {
        return new FileCopyId(uuid);
    }

    public String getValue(FilePath filePath) {
        return filePath.toString();
    }

    public FilePath toFilePath(String value) {
        return new FilePath(value);
    }

    public String getValue(FileCopyFailureReason reason) {
        return reason.value();
    }

    public FileCopyFailureReason toFileCopyFailureReason(String reason) {
        return new FileCopyFailureReason(reason);
    }
}
