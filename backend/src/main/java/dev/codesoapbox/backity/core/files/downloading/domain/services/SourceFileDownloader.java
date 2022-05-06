package dev.codesoapbox.backity.core.files.downloading.domain.services;

import dev.codesoapbox.backity.core.files.downloading.domain.model.EnqueuedFileDownload;

import java.io.IOException;

public interface SourceFileDownloader {

    String getSource();

    void downloadGameFile(EnqueuedFileDownload enqueuedFileDownload, String tempFilePath) throws IOException;

    boolean isReady();
}
