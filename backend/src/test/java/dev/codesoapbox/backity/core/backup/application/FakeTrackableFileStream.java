package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgress;
import dev.codesoapbox.backity.core.backup.application.exceptions.FileDownloadWasCanceledException;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.springframework.core.io.buffer.DataBufferUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@Accessors(fluent = true)
class FakeTrackableFileStream implements TrackableFileStream {

    private final DownloadProgress downloadProgress = mock();

    @Getter
    private final String data = "data";

    @Setter
    private Runnable triggerOnWrite;

    public FakeTrackableFileStream() {
        lenient().when(downloadProgress.getContentLengthBytes())
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
            throw new FileDownloadWasCanceledException(filePath);
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

    @Override
    public DownloadProgress progress() {
        return downloadProgress;
    }
}