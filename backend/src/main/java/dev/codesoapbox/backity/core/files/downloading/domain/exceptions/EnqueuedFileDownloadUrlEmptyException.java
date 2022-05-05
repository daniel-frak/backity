package dev.codesoapbox.backity.core.files.downloading.domain.exceptions;

import dev.codesoapbox.backity.core.files.downloading.domain.model.EnqueuedFileDownload;

public class EnqueuedFileDownloadUrlEmptyException extends IllegalArgumentException {

    public EnqueuedFileDownloadUrlEmptyException(Long id) {
        super("Game file url was null or empty for " + EnqueuedFileDownload.class.getSimpleName() + " with id: " + id);
    }
}
