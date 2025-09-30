package dev.codesoapbox.backity.core.backup.application.exceptions;

import dev.codesoapbox.backity.core.backup.application.WriteDestination;

public class ConcurrentFileWriteException extends RuntimeException {

    public ConcurrentFileWriteException(WriteDestination writeDestination) {
        super(String.format("File '%s' in storage solution %s is currently being written to by another thread",
                writeDestination.filePath(), writeDestination.storageSolutionId()));
    }
}
