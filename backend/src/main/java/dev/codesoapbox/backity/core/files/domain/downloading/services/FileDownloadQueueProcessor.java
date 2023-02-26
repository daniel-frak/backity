package dev.codesoapbox.backity.core.files.domain.downloading.services;

import dev.codesoapbox.backity.core.files.domain.downloading.model.GameFileVersion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RequiredArgsConstructor
public class FileDownloadQueueProcessor {

    private final FileDownloadQueue fileDownloadQueue;
    private final FileDownloader fileDownloader;

    final AtomicReference<GameFileVersion> enqueuedFileDownloadReference = new AtomicReference<>();

    public synchronized void processQueue() {
        if (enqueuedFileDownloadReference.get() != null) {
            return;
        }

        fileDownloadQueue.getOldestWaiting()
                .ifPresent(this::processEnqueuedFileDownload);
    }

    private void processEnqueuedFileDownload(GameFileVersion gameFileVersion) {
        if (!fileDownloader.isReadyFor(gameFileVersion)) {
            return;
        }

        enqueuedFileDownloadReference.set(gameFileVersion);

        log.info("Downloading enqueued file {}", gameFileVersion.getUrl());

        try {
            fileDownloadQueue.markInProgress(gameFileVersion);
            String filePath = fileDownloader.downloadGameFile(gameFileVersion);
            fileDownloadQueue.acknowledgeSuccess(gameFileVersion, filePath);
        } catch (RuntimeException e) {
            log.error("An error occurred while trying to process enqueued file (id: {})",
                    gameFileVersion.getId(), e);
            fileDownloadQueue.acknowledgeFailed(gameFileVersion, e.getMessage());
        } finally {
            enqueuedFileDownloadReference.set(null);
        }
    }
}
