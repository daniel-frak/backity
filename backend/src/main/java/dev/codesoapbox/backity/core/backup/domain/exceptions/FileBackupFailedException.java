package dev.codesoapbox.backity.core.backup.domain.exceptions;

import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetails;

public class FileBackupFailedException extends RuntimeException {

    public FileBackupFailedException(GameFileDetails gameFileDetails, Throwable cause) {
        super("Could not back up game file " + gameFileDetails.getId().value(), cause);
    }
}
