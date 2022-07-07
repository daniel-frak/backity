package dev.codesoapbox.backity.core.files.downloading.domain.services;

import dev.codesoapbox.backity.core.files.downloading.domain.model.EnqueuedFileDownload;

public interface FileDownloadMessageService {

    void sendDownloadStarted(EnqueuedFileDownload payload);
    void sendDownloadFinished(EnqueuedFileDownload payload);
}
