package dev.codesoapbox.backity.files.downloading.domain.services;

import dev.codesoapbox.backity.files.downloading.domain.model.EnqueuedFileDownload;

public interface SourceFileDownloader {

    String getSource();

    void downloadGameFile(EnqueuedFileDownload enqueuedFileDownload, String tempFilePath);
}
