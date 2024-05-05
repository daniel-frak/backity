package dev.codesoapbox.backity.core.backup.domain;

import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RequiredArgsConstructor
public class EnqueuedFileBackupProcessor {

    private final FileDetailsRepository fileDetailsRepository;
    private final FileBackupService fileBackupService;
    private final FileBackupMessageService messageService;

    final AtomicReference<FileDetails> enqueuedFileBackupReference = new AtomicReference<>();

    public synchronized void processQueue() {
        if (enqueuedFileBackupReference.get() != null) {
            return;
        }

        fileDetailsRepository.findOldestWaitingForDownload()
                .ifPresent(this::processEnqueuedFileDownload);
    }

    private void processEnqueuedFileDownload(FileDetails fileDetails) {
        if (!fileBackupService.isReadyFor(fileDetails)) {
            return;
        }

        enqueuedFileBackupReference.set(fileDetails);

        log.info("Backing up enqueued file {}", fileDetails.getSourceFileDetails().url());

        try {
            messageService.sendBackupStarted(fileDetails);
            fileBackupService.backUpFile(fileDetails);
            messageService.sendBackupFinished(fileDetails);
        } catch (RuntimeException e) {
            log.error("An error occurred while trying to process enqueued file (id: {})",
                    fileDetails.getId(), e);
            messageService.sendBackupFinished(fileDetails);
        } finally {
            enqueuedFileBackupReference.set(null);
        }
    }
}
