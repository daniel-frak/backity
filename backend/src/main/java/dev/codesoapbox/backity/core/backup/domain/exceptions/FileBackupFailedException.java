package dev.codesoapbox.backity.core.backup.domain.exceptions;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;

public class FileBackupFailedException extends RuntimeException {

    public FileBackupFailedException(SourceFile sourceFile, FileCopy fileCopy, Throwable cause) {
        super("Could not back up source file " + sourceFile.getId().value()
              + " (file copy " + fileCopy.getId() + ")", cause);
    }
}
