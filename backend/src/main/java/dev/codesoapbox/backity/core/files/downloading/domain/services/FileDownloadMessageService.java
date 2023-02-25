package dev.codesoapbox.backity.core.files.downloading.domain.services;

import dev.codesoapbox.backity.core.files.downloading.domain.model.EnqueuedFileDownload;
import dev.codesoapbox.backity.core.files.downloading.domain.model.messages.FileDownloadProgress;

public interface FileDownloadMessageService {

    void sendDownloadStarted(EnqueuedFileDownload payload);

    void sendProgress(FileDownloadProgress payload);

    void sendDownloadFinished(EnqueuedFileDownload payload);
}
