package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.writeprogress.OutputStreamProgressTracker;
import dev.codesoapbox.backity.core.backup.application.exceptions.ConcurrentFileWriteException;
import dev.codesoapbox.backity.core.backup.application.exceptions.StorageSolutionWriteFailedException;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
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

    private final ConcurrentHashMap<String, ActiveWrite> activeWritesByFilePath = new ConcurrentHashMap<>();

    public void writeFileToStorage(
            TrackableFileStream trackableFileStream, StorageSolution storageSolution, String filePath) {
        initializeCancellationTracker(filePath);
        try {
            Flux<Boolean> cancelTrigger = getCancelTrigger(filePath);
            trackableFileStream.writeToStorageSolution(storageSolution, filePath, cancelTrigger);

            OutputStreamProgressTracker outputStreamProgressTracker = trackableFileStream.outputStreamProgressTracker();
            validateWrittenFileSize(storageSolution, filePath, outputStreamProgressTracker.getContentLengthBytes());
        } finally {
            activeWritesByFilePath.remove(filePath);
        }
    }

    private void initializeCancellationTracker(String filePath) {
        ActiveWrite existingActiveWrite =
                activeWritesByFilePath.putIfAbsent(filePath, new ActiveWrite());
        if (existingActiveWrite != null) {
            throw new ConcurrentFileWriteException(filePath);
        }
    }

    private Flux<Boolean> getCancelTrigger(String filePath) {
        return activeWritesByFilePath.get(filePath).getCancelSignal().asFlux();
    }

    private void validateWrittenFileSize(
            StorageSolution storageSolution, String filePath, long expectedSizeInBytes) {
        long sizeInBytesOnDisk = storageSolution.getSizeInBytes(filePath);
        if (sizeInBytesOnDisk != expectedSizeInBytes) {
            throw new StorageSolutionWriteFailedException(
                    "The written size of " + filePath + " is not what was expected (was "
                            + sizeInBytesOnDisk + ", expected " + expectedSizeInBytes + ")");
        } else {
            log.info("Filesize check for {} passed successfully", filePath);
        }
    }

    public void cancelWrite(@NonNull String filePath) {
        ActiveWrite activeWrite = activeWritesByFilePath.get(filePath);
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
