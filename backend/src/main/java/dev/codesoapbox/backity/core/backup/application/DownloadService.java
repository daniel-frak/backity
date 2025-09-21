package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgress;
import dev.codesoapbox.backity.core.backup.application.exceptions.ConcurrentFileDownloadException;
import dev.codesoapbox.backity.core.backup.application.exceptions.FileDownloadFailedException;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.concurrent.ConcurrentHashMap;

// @TODO Rename to something that better reflects what it does vs FileCopyReplicator?
@Slf4j
@RequiredArgsConstructor
public class DownloadService {

    private final ConcurrentHashMap<String, ActiveDownload> activeDownloadsByFilePath = new ConcurrentHashMap<>();

    public void downloadFile(StorageSolution storageSolution, TrackableFileStream trackableFileStream,
                             GameFile gameFile, String filePath) {
        initializeCancellationTracker(filePath);
        try {
            Flux<Boolean> cancelTrigger = getCancelTrigger(filePath);
            trackableFileStream.writeToStorageSolution(storageSolution, filePath, cancelTrigger);

            log.info("Downloaded file {} to {}", gameFile, filePath);
            DownloadProgress progress = trackableFileStream.progress();
            validateDownloadedFileSize(storageSolution, filePath, progress.getContentLengthBytes());
        } finally {
            activeDownloadsByFilePath.remove(filePath);
        }
    }

    private void initializeCancellationTracker(String filePath) {
        ActiveDownload existingActiveDownload =
                activeDownloadsByFilePath.putIfAbsent(filePath, new ActiveDownload());
        if (existingActiveDownload != null) {
            throw new ConcurrentFileDownloadException(filePath);
        }
    }

    private Flux<Boolean> getCancelTrigger(String filePath) {
        return activeDownloadsByFilePath.get(filePath).getCancelSignal().asFlux();
    }

    private void validateDownloadedFileSize(
            StorageSolution storageSolution, String filePath, long expectedSizeInBytes) {
        long sizeInBytesOnDisk = storageSolution.getSizeInBytes(filePath);
        if (sizeInBytesOnDisk != expectedSizeInBytes) {
            throw new FileDownloadFailedException(
                    "The downloaded size of " + filePath + " is not what was expected (was "
                            + sizeInBytesOnDisk + ", expected " + expectedSizeInBytes + ")");
        } else {
            log.info("Filesize check for {} passed successfully", filePath);
        }
    }

    public void cancelDownload(@NonNull String filePath) {
        ActiveDownload activeDownload = activeDownloadsByFilePath.get(filePath);
        if (activeDownload != null) {
            activeDownload.triggerCancellation();
        }
    }

    @Getter
    private static class ActiveDownload {

        private final Sinks.Many<Boolean> cancelSignal = Sinks.many().replay().latest();

        public void triggerCancellation() {
            cancelSignal.tryEmitNext(true);
        }
    }
}
