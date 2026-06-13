package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.exceptions.ConcurrentFileWriteException;
import dev.codesoapbox.backity.core.backup.application.exceptions.StorageSolutionWriteFailedException;
import dev.codesoapbox.backity.core.backup.application.writeprogress.OutputStreamProgressTracker;
import dev.codesoapbox.backity.core.storagesolution.domain.FilePath;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.UniqueFilePathResolver;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
public class StorageSolutionWriteService {

    /// [UniqueFilePathResolver] can only check for uniqueness against files which already exist, and therefore
    /// cannot guarantee currently active backups are unique amongst themselves
    /// (as their files may not exist at the time of validation).
    ///
    /// This map can be used to ensure multiple active backups are not writing to the same file at the same time.
    ///
    /// Warning! This alone is not enough to protect against overwriting existing files
    /// if multiple instances of this application are running.
    /// If multiple instances may be running, it is likely best to only allow one of them to perform backups at a time
    /// (e.g., by using a distributed lock).
    private final ConcurrentHashMap<WriteDestination, ActiveWrite> activeWritesByDestination =
            new ConcurrentHashMap<>();

    public void writeFileToStorage(
            TrackableFileStream trackableFileStream, StorageSolution storageSolution, FilePath filePath) {
        var writeDestination = new WriteDestination(storageSolution.getId(), filePath);
        initializeCancellationTracker(writeDestination);
        try {
            Flux<Boolean> cancelTrigger = getCancelTrigger(writeDestination);
            trackableFileStream.writeToStorageSolution(storageSolution, filePath, cancelTrigger);

            OutputStreamProgressTracker outputStreamProgressTracker = trackableFileStream.outputStreamProgressTracker();
            validateWrittenFileSize(storageSolution, filePath, outputStreamProgressTracker.getContentLengthBytes());
        } finally {
            activeWritesByDestination.remove(writeDestination);
        }
    }

    private void initializeCancellationTracker(WriteDestination writeDestination) {
        ActiveWrite existingActiveWrite =
                activeWritesByDestination.putIfAbsent(writeDestination, new ActiveWrite());
        if (existingActiveWrite != null) {
            throw new ConcurrentFileWriteException(writeDestination);
        }
    }

    private Flux<Boolean> getCancelTrigger(WriteDestination writeDestination) {
        return activeWritesByDestination.get(writeDestination).getCancelSignal().asFlux();
    }

    private void validateWrittenFileSize(
            StorageSolution storageSolution, FilePath filePath, long expectedSizeInBytes) {
        long sizeInBytesOnDisk = storageSolution.getSizeInBytes(filePath);
        if (sizeInBytesOnDisk != expectedSizeInBytes) {
            throw new StorageSolutionWriteFailedException(
                    "The written size of " + filePath + " is not what was expected (was "
                            + sizeInBytesOnDisk + ", expected " + expectedSizeInBytes + ")");
        } else {
            log.info("Filesize check for {} passed successfully", filePath);
        }
    }

    public void cancelWrite(@NonNull WriteDestination writeDestination) {
        ActiveWrite activeWrite = activeWritesByDestination.get(writeDestination);
        if (activeWrite != null) {
            activeWrite.triggerCancellation();
        }
    }

    @Getter
    private static class ActiveWrite {

        private final Sinks.Many<Boolean> cancelSignal = Sinks.many().replay().latest();

        public void triggerCancellation() {
            cancelSignal.tryEmitNext(true);
        }
    }
}
