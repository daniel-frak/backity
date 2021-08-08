package dev.codesoapbox.gogbackupservice.files.downloading.domain.services;

import dev.codesoapbox.gogbackupservice.files.downloading.domain.model.EnqueuedFileDownload;

public interface SourceFileDownloader {

    String getSource();

    void downloadGameFile(EnqueuedFileDownload enqueuedFileDownload, String tempFilePath);
}
