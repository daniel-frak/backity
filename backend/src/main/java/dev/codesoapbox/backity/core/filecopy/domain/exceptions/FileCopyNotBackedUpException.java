package dev.codesoapbox.backity.core.filecopy.domain.exceptions;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.shared.domain.exceptions.DomainInvariantViolationException;

public class FileCopyNotBackedUpException extends DomainInvariantViolationException {

    public FileCopyNotBackedUpException(FileCopyId fileCopyId) {
        super("FileCopy (id=" + fileCopyId + ") is not backed up");
    }
}
