package dev.codesoapbox.backity.core.files.domain.backup.exceptions;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersionBackup;

public class FileBackupFailedException extends RuntimeException {

    public FileBackupFailedException(GameFileVersionBackup gameFileVersionBackup, Throwable cause) {
        super("Could not back up game file " + gameFileVersionBackup.getId(), cause);
    }
}
