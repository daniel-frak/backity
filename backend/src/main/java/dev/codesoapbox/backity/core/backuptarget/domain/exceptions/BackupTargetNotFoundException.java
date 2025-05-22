package dev.codesoapbox.backity.core.backuptarget.domain.exceptions;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.shared.domain.exceptions.DomainInvariantViolationException;

public class BackupTargetNotFoundException extends DomainInvariantViolationException {

    public BackupTargetNotFoundException(BackupTargetId id) {
        super("Could not find " + BackupTarget.class.getSimpleName() + " with id=" + id);
    }
}
