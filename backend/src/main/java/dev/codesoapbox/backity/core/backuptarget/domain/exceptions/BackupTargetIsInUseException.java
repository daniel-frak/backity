package dev.codesoapbox.backity.core.backuptarget.domain.exceptions;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;

public class BackupTargetIsInUseException extends RuntimeException {

    public BackupTargetIsInUseException(BackupTargetId id) {
        super(BackupTarget.class.getSimpleName() + " with id=" + id + " is in use");
    }
}
