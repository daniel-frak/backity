package dev.codesoapbox.backity.core.backup.application.usecases;

import dev.codesoapbox.backity.core.backup.application.FileBackupContext;
import dev.codesoapbox.backity.core.backup.application.FileBackupContextFactory;
import dev.codesoapbox.backity.core.backup.application.FileBackupService;
import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProcess;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class ProcessFileCopyQueueUseCase {

    private final FileCopyReplicationProcess fileCopyReplicationProcess;
    private final FileCopyRepository fileCopyRepository;
    private final FileBackupContextFactory fileBackupContextFactory;
    private final FileBackupService fileBackupService;

    @SuppressWarnings(
            // I was not able to come up with an implementation with less nesting which would preserve readability:
            "java:S134"
    )
    public void processFileCopyQueue() {
        /*
         After markAsCompleted(), tryStart() is called again to catch any items
         enqueued while the previous run was in progress.
        */
        while (fileCopyReplicationProcess.tryStart()) {
            try {
                Optional<FileCopy> maybeCurrentFileCopy = fileCopyRepository.findOldestEnqueued();

                if (maybeCurrentFileCopy.isEmpty()) {
                    return;
                }

                FileCopy previousFileCopy = null;

                do {
                    FileCopy currentFileCopy = maybeCurrentFileCopy.get();

                    if (isDuplicate(previousFileCopy, currentFileCopy)) {
                        log.warn("File Copy queue encountered the same file copy (id={}) twice in a row."
                                + "Stopping processing to avoid infinite loop.", currentFileCopy.getId());
                        /*
                         An alternative would be to mark the FileCopy as FAILED.
                         However, a situation like this suggests deeper issues, likely to do with persistence,
                         which have more to do with infrastructure or code than the particular FileCopy.
                         Therefore, it seems best to just stop the queue until the problem is fixed.
                        */
                        return;
                    }

                    previousFileCopy = currentFileCopy;
                    tryToBackUp(currentFileCopy);

                    maybeCurrentFileCopy = fileCopyRepository.findOldestEnqueued();
                } while (maybeCurrentFileCopy.isPresent());
            } finally {
                fileCopyReplicationProcess.markAsCompleted();
            }
        }
    }

    private boolean isDuplicate(FileCopy previousFileCopy, FileCopy currentFileCopy) {
        return previousFileCopy != null && previousFileCopy.equals(currentFileCopy);
    }

    private void tryToBackUp(FileCopy fileCopy) {
        try {
            backUp(fileCopy);
        } catch (RuntimeException e) {
            /*
             This relies on FileBackupService#backUpFile having proper exception handling, so that FileCopies which
             fail get marked as FAILED. Failed FileCopies are not part of the queue until they are manually retried.
            */
            log.error("An error occurred while trying to process enqueued file copy (id={})",
                    fileCopy.getId(), e);
        }
    }

    /*
    Note that the current implementation won't mark the FileCopy as "FAILED" when one of the repository calls fails,
    potentially leading to backups getting stuck, as the failing backup will always be the first in the queue.
    This is probably not a problem, though, the fileBackupContextFactory failing is likely to be a problem unrelated
    to the FileCopy itself.
     */
    private void backUp(FileCopy fileCopy) {
        FileBackupContext fileBackupContext = fileBackupContextFactory.create(fileCopy);
        fileBackupService.backUpFile(fileBackupContext);
    }
}
