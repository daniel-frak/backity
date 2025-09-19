package dev.codesoapbox.backity.core.backup.application.usecases;

import dev.codesoapbox.backity.core.backup.application.FileBackupContext;
import dev.codesoapbox.backity.core.backup.application.FileBackupContextFactory;
import dev.codesoapbox.backity.core.backup.application.FileBackupService;
import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProcess;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class BackUpOldestFileCopyUseCase {

    private final FileCopyReplicationProcess fileCopyReplicationProcess;
    private final FileCopyRepository fileCopyRepository;
    private final FileBackupContextFactory fileBackupContextFactory;
    private final FileBackupService fileBackupService;

    public synchronized void backUpOldestFileCopy() {
        if (!fileCopyReplicationProcess.canStart()) {
            return;
        }

        fileCopyReplicationProcess.markAsInProgress();
        try {
            fileCopyRepository.findOldestEnqueued()
                    .ifPresent(this::tryToBackUp);
        } finally {
            fileCopyReplicationProcess.markAsCompleted();
        }
    }

    private void tryToBackUp(FileCopy fileCopy) {
        try {
            backUp(fileCopy);
        } catch (RuntimeException e) {
            log.error("An error occurred while trying to process enqueued game file (id: {})",
                    fileCopy.getId(), e);
        }
    }

    /*
    Note that the current implementation won't mark the FileCopy as "FAILED" when one of the repository calls fails,
    potentially leading to backups getting stuck, as the failing backup will always be the first in the queue.
    This is probably not a problem, though, as these repository calls failing is likely to be a problem unrelated
    to the FileCopy itself.
     */
    private void backUp(FileCopy fileCopy) {
        FileBackupContext fileBackupContext = fileBackupContextFactory.create(fileCopy);
        fileBackupService.backUpFile(fileBackupContext);
    }
}
