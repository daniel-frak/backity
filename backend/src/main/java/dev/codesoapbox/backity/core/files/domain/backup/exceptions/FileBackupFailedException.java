package dev.codesoapbox.backity.core.files.domain.backup.exceptions;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;

public class FileBackupFailedException extends RuntimeException {

    public FileBackupFailedException(GameFileDetails gameFileDetails, Throwable cause) {
        super("Could not back up game file " + gameFileDetails.getId().value(), cause);
    }
}
