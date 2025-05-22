package dev.codesoapbox.backity.core.filecopy.domain.exceptions;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.shared.domain.exceptions.DomainInvariantViolationException;

public class FilePathMustNotBeNullForSuccessfulFileCopy extends DomainInvariantViolationException {

    public FilePathMustNotBeNullForSuccessfulFileCopy(FileCopyId fileCopyId) {
        super("File path must not be null for successful file copy (id=" + fileCopyId + ")");
    }
}
