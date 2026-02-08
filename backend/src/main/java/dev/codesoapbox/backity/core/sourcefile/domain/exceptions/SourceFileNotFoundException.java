package dev.codesoapbox.backity.core.sourcefile.domain.exceptions;

import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFileId;
import dev.codesoapbox.backity.shared.domain.exceptions.DomainInvariantViolationException;

public class SourceFileNotFoundException extends DomainInvariantViolationException {

    public SourceFileNotFoundException(SourceFileId id) {
        super("Could not find " + SourceFile.class.getSimpleName() + " with id=" + id);
    }
}
