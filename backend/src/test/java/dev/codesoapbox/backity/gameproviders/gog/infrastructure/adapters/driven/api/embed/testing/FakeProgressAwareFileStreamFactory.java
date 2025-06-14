package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.embed.testing;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgress;
import dev.codesoapbox.backity.core.backup.application.TrackableFileStream;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import reactor.core.publisher.Flux;

import java.time.Clock;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
public class FakeProgressAwareFileStreamFactory {

    private final Clock clock;

    public TrackableFileStream create(DownloadProgress progress, String data) {
        progress.initializeTracking(data.getBytes().length, clock);
        byte[] bytes = data.getBytes();
        DefaultDataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(bytes);

        return new TrackableFileStream(Flux.just(dataBuffer), progress);
    }

    public TrackableFileStream createInfiniteStream(DownloadProgress progress, AtomicBoolean shouldStop) {
        progress.initializeTracking(Long.MAX_VALUE, clock);

        byte[] bytes = "Test data".getBytes();
        DefaultDataBuffer buffer = new DefaultDataBufferFactory().wrap(bytes);
        Flux<DataBuffer> infiniteFlux = Flux.generate(sink -> {
            sink.next(buffer);
            if (shouldStop.get()) {
                sink.complete();
            }
        });

        return new TrackableFileStream(infiniteFlux, progress);
    }
}
