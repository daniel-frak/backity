package dev.codesoapbox.backity.core.files.domain.backup.exceptions;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersion;

public class FileBackupFailedException extends RuntimeException {

    public FileBackupFailedException(GameFileVersion gameFileVersion, Throwable cause) {
        super("Could not back up game file " + gameFileVersion.getId(), cause);
    }
}
