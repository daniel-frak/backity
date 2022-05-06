package dev.codesoapbox.backity.core.files.downloading.domain.exceptions;

import dev.codesoapbox.backity.core.files.downloading.domain.model.EnqueuedFileDownload;

public class FileDownloadFailedException extends RuntimeException {

    public FileDownloadFailedException(EnqueuedFileDownload enqueuedFileDownload, Throwable cause) {
        super("Could not download game file " + enqueuedFileDownload.getId(), cause);
    }
}
