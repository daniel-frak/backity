package dev.codesoapbox.backity.core.filecopy.domain.exceptions;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.shared.domain.exceptions.DomainInvariantViolationException;

public class FileCopyNotFoundException extends DomainInvariantViolationException {

    public FileCopyNotFoundException(FileCopyId id) {
        super("Could not find " + FileCopy.class.getSimpleName() + " with id=" + id);
    }

    public FileCopyNotFoundException(GameFileId gameFileId, BackupTargetId backupTargetId) {
        super("Could not find " + FileCopy.class.getSimpleName() + " with gameFileId=" + gameFileId
              + ", backupTargetId=" + backupTargetId);
    }
}
