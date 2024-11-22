package dev.codesoapbox.backity.core.backup.domain.exceptions;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;

public class FileBackupUrlEmptyException extends IllegalArgumentException {

    public FileBackupUrlEmptyException(GameFileId id) {
        super("Game file url was null or empty for " + GameFile.class.getSimpleName()
                + " with id: " + id.value());
    }
}
