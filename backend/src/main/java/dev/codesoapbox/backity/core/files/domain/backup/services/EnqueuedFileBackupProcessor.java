package dev.codesoapbox.backity.core.files.domain.backup.services;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileDetailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RequiredArgsConstructor
public class EnqueuedFileBackupProcessor {

    private final GameFileDetailsRepository gameFileDetailsRepository;
    private final FileBackupService fileBackupService;
    private final FileBackupMessageService messageService;

    // @TODO Refactor this so the processQueue method doesn't have to be synchronized
    final AtomicReference<GameFileDetails> enqueuedFileBackupReference = new AtomicReference<>();

    public synchronized void processQueue() {
        if (enqueuedFileBackupReference.get() != null) {
            return;
        }

        gameFileDetailsRepository.findOldestWaitingForDownload()
                .ifPresent(this::processEnqueuedFileDownload);
    }

    private void processEnqueuedFileDownload(GameFileDetails gameFileDetails) {
        if (!fileBackupService.isReadyFor(gameFileDetails)) {
            return;
        }

        enqueuedFileBackupReference.set(gameFileDetails);

        log.info("Backing up enqueued file {}", gameFileDetails.getUrl());

        try {
            messageService.sendBackupStarted(gameFileDetails);
            fileBackupService.backUpGameFile(gameFileDetails);
            messageService.sendBackupFinished(gameFileDetails);
        } catch (RuntimeException e) {
            log.error("An error occurred while trying to process enqueued file (id: {})",
                    gameFileDetails.getId(), e);
            messageService.sendBackupFinished(gameFileDetails);
        } finally {
            enqueuedFileBackupReference.set(null);
        }
    }
}
