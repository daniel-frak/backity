package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.embed.testing;

import dev.codesoapbox.backity.core.backup.application.TrackableFileStream;
import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgress;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import reactor.core.publisher.Flux;

import java.time.Clock;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class FakeTrackableFileStreamFactory {

    private final Clock clock;

    public TrackableFileStream create(DownloadProgress progress, String data) {
        progress.initializeTracking(data.getBytes().length, clock);
        byte[] bytes = data.getBytes();
        DefaultDataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(bytes);

        return new TrackableFileStream(Flux.just(dataBuffer), progress);
    }

    public TrackableFileStream createFailing(DownloadProgress progress, String data, Throwable exception) {
        progress.initializeTracking(data.getBytes().length, clock);

        return new TrackableFileStream(Flux.error(exception), progress);
    }

    public TrackableFileStream createInitiallyFailing(DownloadProgress progress, String data,
                                                      List<Throwable> exceptions, AtomicInteger numOfTriesTracker) {
        progress.initializeTracking(data.getBytes().length, clock);
        byte[] bytes = data.getBytes();
        DefaultDataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(bytes);
        Deque<Throwable> exceptionQueue = new ArrayDeque<>(exceptions);

        Flux<DataBuffer> flakyFlux = Flux.defer(() -> {
            numOfTriesTracker.incrementAndGet();
            Throwable exception = exceptionQueue.poll();

            if (exception != null) {
                return Flux.error(exception);
            }

            return Flux.just(dataBuffer);
        });

        return new TrackableFileStream(flakyFlux, progress);
    }

    public TrackableFileStream createFailingHalfwayThrough(DownloadProgress progress, String data,
                                                           Throwable exception) {
        progress.initializeTracking(data.getBytes().length, clock);
        byte[] bytes = data.getBytes();

        int midPoint = bytes.length / 2;
        byte[] firstChunk = Arrays.copyOfRange(bytes, 0, midPoint);
        byte[] secondChunk = Arrays.copyOfRange(bytes, midPoint, bytes.length);

        DefaultDataBufferFactory bufferFactory = new DefaultDataBufferFactory();
        DefaultDataBuffer firstBuffer = bufferFactory.wrap(firstChunk);
        DefaultDataBuffer secondBuffer = bufferFactory.wrap(secondChunk);

        var hasThrownError = new AtomicBoolean(false);

        return new TrackableFileStream(
                Flux.concat(
                        Flux.just(firstBuffer),
                        Flux.defer(() -> hasThrownError.compareAndSet(false, true)
                                ? Flux.error(exception)
                                : Flux.just(secondBuffer))
                ),
                progress
        );
    }

}
