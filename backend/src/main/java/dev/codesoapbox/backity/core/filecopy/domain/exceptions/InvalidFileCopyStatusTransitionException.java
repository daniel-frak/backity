package dev.codesoapbox.backity.core.filecopy.domain.exceptions;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;
import dev.codesoapbox.backity.shared.domain.exceptions.DomainInvariantViolationException;

public class InvalidFileCopyStatusTransitionException extends DomainInvariantViolationException {

    public InvalidFileCopyStatusTransitionException(FileCopyId fileCopyId, FileCopyStatus from, FileCopyStatus to) {
        super("Invalid FileCopy status transition: " + from + " -> " + to
              + " (id=" + fileCopyId + ")");
    }
}
