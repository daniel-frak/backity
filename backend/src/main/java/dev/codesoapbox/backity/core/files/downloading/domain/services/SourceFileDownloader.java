package dev.codesoapbox.backity.core.files.downloading.domain.services;

import dev.codesoapbox.backity.core.files.downloading.domain.model.EnqueuedFileDownload;

public interface SourceFileDownloader {

    String getSource();

    void downloadGameFile(EnqueuedFileDownload enqueuedFileDownload, String tempFilePath);

    boolean isReady();
}
