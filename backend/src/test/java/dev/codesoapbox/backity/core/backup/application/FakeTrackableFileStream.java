package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.writeprogress.OutputStreamProgressTracker;
import dev.codesoapbox.backity.core.backup.application.exceptions.FileWriteWasCanceledException;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@Accessors(fluent = true)
class FakeTrackableFileStream implements TrackableFileStream {

    private final OutputStreamProgressTracker outputStreamProgressTracker = mock();

    @Getter
    private final String data = "data";

    @Setter
    private Runnable triggerOnWrite;

    public FakeTrackableFileStream() {
        lenient().when(outputStreamProgressTracker.getContentLengthBytes())
                .thenReturn((long) data.getBytes().length);
    }

    @SneakyThrows
    @Override
    public void writeToStorageSolution(StorageSolution storageSolution, String filePath, Flux<Boolean> cancelTrigger) {
        Mono<Boolean> writeWasCancelledMono = Mono.firstWithSignal(
                cancelTrigger.next().thenReturn(true),
                Mono.defer(() -> {
                    writeToDisk(storageSolution, filePath);
                    return Mono.just(false);
                })
        );
        boolean writeWasCancelled = Boolean.TRUE.equals(writeWasCancelledMono.block());
        if (writeWasCancelled) {
            throw new FileWriteWasCanceledException(filePath, storageSolution);
        }
    }

    @SneakyThrows
    private void writeToDisk(StorageSolution storageSolution, String filePath) {
        if(triggerOnWrite != null) {
            triggerOnWrite.run();
        }
        storageSolution.getOutputStream(filePath)
                .write(data.getBytes());
    }

    public OutputStreamProgressTracker outputStreamProgressTracker() {
        return outputStreamProgressTracker;
    }
}