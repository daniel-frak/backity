package dev.codesoapbox.backity.core.files.domain.backup.exceptions;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersionBackup;

public class FileBackupUrlEmptyException extends IllegalArgumentException {

    public FileBackupUrlEmptyException(Long id) {
        super("Game file url was null or empty for " + GameFileVersionBackup.class.getSimpleName() + " with id: " + id);
    }
}
