package dev.codesoapbox.backity.core.files.domain.downloading.services;

import dev.codesoapbox.backity.core.files.domain.downloading.model.GameFileVersion;
import dev.codesoapbox.backity.core.files.domain.downloading.repositories.GameFileVersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RequiredArgsConstructor
public class EnqueuedFileDownloadProcessor {

    private final GameFileVersionRepository gameFileVersionRepository;
    private final FileDownloader fileDownloader;
    private final FileDownloadMessageService messageService;

    // @TODO Refactor this so the processQueue method doesn't have to be synchronized
    final AtomicReference<GameFileVersion> enqueuedFileDownloadReference = new AtomicReference<>();

    public synchronized void processQueue() {
        if (enqueuedFileDownloadReference.get() != null) {
            return;
        }

        gameFileVersionRepository.findOldestWaitingForDownload()
                .ifPresent(this::processEnqueuedFileDownload);
    }

    private void processEnqueuedFileDownload(GameFileVersion gameFileVersion) {
        if (!fileDownloader.isReadyFor(gameFileVersion)) {
            return;
        }

        enqueuedFileDownloadReference.set(gameFileVersion);

        log.info("Downloading enqueued file {}", gameFileVersion.getUrl());

        try {
            messageService.sendDownloadStarted(gameFileVersion);
            fileDownloader.downloadGameFile(gameFileVersion);
            messageService.sendDownloadFinished(gameFileVersion);
        } catch (RuntimeException e) {
            log.error("An error occurred while trying to process enqueued file (id: {})",
                    gameFileVersion.getId(), e);
            messageService.sendDownloadFinished(gameFileVersion);
        } finally {
            enqueuedFileDownloadReference.set(null);
        }
    }
}
