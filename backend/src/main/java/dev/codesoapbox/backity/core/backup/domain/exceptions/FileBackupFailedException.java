package dev.codesoapbox.backity.core.backup.domain.exceptions;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;

public class FileBackupFailedException extends RuntimeException {

    public FileBackupFailedException(GameFile gameFile, FileCopy fileCopy, Throwable cause) {
        super("Could not back up game file " + gameFile.getId().value()
              + " (file copy " + fileCopy.getId() + ")", cause);
    }
}
