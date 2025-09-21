package dev.codesoapbox.backity.shared.infrastructure.adapters.driven.api.spring;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgress;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RequiredArgsConstructor
public class DataBufferFluxTrackableFileStreamFactory {

    private final int maxRetryAttempts;
    private final Duration retryBackoff;

    public DataBufferFluxTrackableFileStream create(
            Flux<DataBuffer> dataBufferFlux, DownloadProgress downloadProgress) {
        return new DataBufferFluxTrackableFileStream(dataBufferFlux, downloadProgress, maxRetryAttempts, retryBackoff);
    }
}
