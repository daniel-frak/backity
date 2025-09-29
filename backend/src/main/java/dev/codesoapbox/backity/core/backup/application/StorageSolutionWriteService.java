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

    private final ConcurrentHashMap<WriteDestination, ActiveWrite> activeWritesByDestination =
            new ConcurrentHashMap<>();

    public void writeFileToStorage(
            TrackableFileStream trackableFileStream, StorageSolution storageSolution, String filePath) {
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
