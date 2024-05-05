package dev.codesoapbox.backity.core.backup.domain.exceptions;

import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;

public class FileBackupFailedException extends RuntimeException {

    public FileBackupFailedException(FileDetails fileDetails, Throwable cause) {
        super("Could not back up game file " + fileDetails.getId().value(), cause);
    }
}
