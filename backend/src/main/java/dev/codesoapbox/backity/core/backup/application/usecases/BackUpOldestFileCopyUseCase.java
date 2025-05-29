package dev.codesoapbox.backity.core.backup.application.usecases;

import dev.codesoapbox.backity.core.backup.application.FileBackupService;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RequiredArgsConstructor
public class BackUpOldestFileCopyUseCase {

    final AtomicReference<FileCopy> enqueuedFileCopyReference = new AtomicReference<>();
    private final FileCopyRepository fileCopyRepository;
    private final GameFileRepository gameFileRepository;
    private final FileBackupService fileBackupService;

    public synchronized void backUpOldestFileCopy() {
        if (enqueuedFileCopyReference.get() != null) {
            return;
        }

        fileCopyRepository.findOldestEnqueued()
                .ifPresent(this::tryToBackUp);
    }

    private void tryToBackUp(FileCopy fileCopy) {
        GameFile gameFile = gameFileRepository.getById(fileCopy.getNaturalId().gameFileId());
        if (!fileBackupService.isReadyFor(gameFile)) {
            return;
        }

        enqueuedFileCopyReference.set(fileCopy);

        log.info("Backing up enqueued file {}", gameFile.getFileSource().url());

        try {
            fileBackupService.backUpFile(gameFile, fileCopy);
        } catch (RuntimeException e) {
            log.error("An error occurred while trying to process enqueued game file (id: {})",
                    fileCopy.getId(), e);
        } finally {
            enqueuedFileCopyReference.set(null);
        }
    }
}
