package dev.codesoapbox.backity.core.files.domain.backup.services;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersion;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileVersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RequiredArgsConstructor
public class EnqueuedFileBackupProcessor {

    private final GameFileVersionRepository gameFileVersionRepository;
    private final FileBackupService fileBackupService;
    private final FileBackupMessageService messageService;

    // @TODO Refactor this so the processQueue method doesn't have to be synchronized
    final AtomicReference<GameFileVersion> enqueuedFileBackupReference = new AtomicReference<>();

    public synchronized void processQueue() {
        if (enqueuedFileBackupReference.get() != null) {
            return;
        }

        gameFileVersionRepository.findOldestWaitingForDownload()
                .ifPresent(this::processEnqueuedFileDownload);
    }

    private void processEnqueuedFileDownload(GameFileVersion gameFileVersion) {
        if (!fileBackupService.isReadyFor(gameFileVersion)) {
            return;
        }

        enqueuedFileBackupReference.set(gameFileVersion);

        log.info("Backing up enqueued file {}", gameFileVersion.getUrl());

        try {
            messageService.sendBackupStarted(gameFileVersion);
            fileBackupService.backUpGameFile(gameFileVersion);
            messageService.sendBackupFinished(gameFileVersion);
        } catch (RuntimeException e) {
            log.error("An error occurred while trying to process enqueued file (id: {})",
                    gameFileVersion.getId(), e);
            messageService.sendBackupFinished(gameFileVersion);
        } finally {
            enqueuedFileBackupReference.set(null);
        }
    }
}
