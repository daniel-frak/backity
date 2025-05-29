package dev.codesoapbox.backity.core.filecopy.domain.exceptions;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.shared.domain.exceptions.DomainInvariantViolationException;

public class FilePathMustNotBeNullForStoredFileCopy extends DomainInvariantViolationException {

    public FilePathMustNotBeNullForStoredFileCopy(FileCopyId fileCopyId) {
        super("File path must not be null for stored file copy (id=" + fileCopyId + ")");
    }
}
