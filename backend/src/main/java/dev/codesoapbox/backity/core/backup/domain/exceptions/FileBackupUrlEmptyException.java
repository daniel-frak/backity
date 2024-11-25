package dev.codesoapbox.backity.core.backup.domain.exceptions;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.core.shared.domain.exceptions.DomainInvariantViolationException;

public class FileBackupUrlEmptyException extends DomainInvariantViolationException {

    public FileBackupUrlEmptyException(GameFileId id) {
        super("Game file url was null or empty for " + GameFile.class.getSimpleName()
                + " with id: " + id.value());
    }
}
