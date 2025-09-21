package dev.codesoapbox.backity.core.backup.application.exceptions;

import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;

public class FileWriteWasCanceledException extends RuntimeException {

    public FileWriteWasCanceledException(String filePath, StorageSolution storageSolution) {
        super(String.format("File write into %s was canceled for '%s'",
                storageSolution.getId(), filePath));
    }
}
