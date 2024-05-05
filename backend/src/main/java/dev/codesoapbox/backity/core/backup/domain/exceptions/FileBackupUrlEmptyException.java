package dev.codesoapbox.backity.core.backup.domain.exceptions;

import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetailsId;

public class FileBackupUrlEmptyException extends IllegalArgumentException {

    public FileBackupUrlEmptyException(FileDetailsId id) {
        super("Game file url was null or empty for " + FileDetails.class.getSimpleName()
                + " with id: " + id.value());
    }
}
