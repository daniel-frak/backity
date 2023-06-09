package dev.codesoapbox.backity.core.files.domain.backup.services;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersionBackup;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileVersionBackupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RequiredArgsConstructor
public class EnqueuedFileBackupProcessor {

    private final GameFileVersionBackupRepository gameFileVersionBackupRepository;
    private final FileBackupService fileBackupService;
    private final FileBackupMessageService messageService;

    // @TODO Refactor this so the processQueue method doesn't have to be synchronized
    final AtomicReference<GameFileVersionBackup> enqueuedFileBackupReference = new AtomicReference<>();

    public synchronized void processQueue() {
        if (enqueuedFileBackupReference.get() != null) {
            return;
        }

        gameFileVersionBackupRepository.findOldestWaitingForDownload()
                .ifPresent(this::processEnqueuedFileDownload);
    }

    private void processEnqueuedFileDownload(GameFileVersionBackup gameFileVersionBackup) {
        if (!fileBackupService.isReadyFor(gameFileVersionBackup)) {
            return;
        }

        enqueuedFileBackupReference.set(gameFileVersionBackup);

        log.info("Backing up enqueued file {}", gameFileVersionBackup.getUrl());

        try {
            messageService.sendBackupStarted(gameFileVersionBackup);
            fileBackupService.backUpGameFile(gameFileVersionBackup);
            messageService.sendBackupFinished(gameFileVersionBackup);
        } catch (RuntimeException e) {
            log.error("An error occurred while trying to process enqueued file (id: {})",
                    gameFileVersionBackup.getId(), e);
            messageService.sendBackupFinished(gameFileVersionBackup);
        } finally {
            enqueuedFileBackupReference.set(null);
        }
    }
}
