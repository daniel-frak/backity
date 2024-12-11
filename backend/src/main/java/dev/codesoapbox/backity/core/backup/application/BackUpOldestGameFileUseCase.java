package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RequiredArgsConstructor
public class BackUpOldestGameFileUseCase {

    private final GameFileRepository gameFileRepository;
    private final FileBackupService fileBackupService;

    final AtomicReference<GameFile> enqueuedFileBackupReference = new AtomicReference<>();

    public synchronized void backUpOldestGameFile() {
        if (enqueuedFileBackupReference.get() != null) {
            return;
        }

        gameFileRepository.findOldestWaitingForDownload()
                .ifPresent(this::tryToBackUp);
    }

    private void tryToBackUp(GameFile gameFile) {
        if (!fileBackupService.isReadyFor(gameFile)) {
            return;
        }

        enqueuedFileBackupReference.set(gameFile);

        log.info("Backing up enqueued file {}", gameFile.getGameProviderFile().url());

        try {
            fileBackupService.backUpFile(gameFile);
        } catch (RuntimeException e) {
            log.error("An error occurred while trying to process enqueued file (id: {})",
                    gameFile.getId(), e);
        } finally {
            enqueuedFileBackupReference.set(null);
        }
    }
}
