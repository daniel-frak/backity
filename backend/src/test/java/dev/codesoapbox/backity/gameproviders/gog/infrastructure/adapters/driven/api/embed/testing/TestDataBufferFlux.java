package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.embed.testing;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import reactor.core.publisher.Flux;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TestDataBufferFlux {

    private static final String SOME_DATA = "Written data";

    public static TestFlux<DataBuffer> succeeding() {
        byte[] bytes = SOME_DATA.getBytes();
        DefaultDataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(bytes);
        Flux<DataBuffer> innerFlux = Flux.just(dataBuffer);
        var attemptsTracker = new AtomicInteger(0);

        return new TestFlux<>(innerFlux, SOME_DATA, attemptsTracker);
    }

    public static TestFlux<DataBuffer> failingHalfwayThroughThenSucceeding(List<Throwable> exceptions) {
        byte[] bytes = SOME_DATA.getBytes();
        Deque<Throwable> exceptionQueue = new ArrayDeque<>(exceptions);
        int dataMidPoint = bytes.length / 2;
        byte[] firstChunk = Arrays.copyOfRange(bytes, 0, dataMidPoint);
        byte[] secondChunk = Arrays.copyOfRange(bytes, dataMidPoint, bytes.length);
        var attemptsTracker = new AtomicInteger(0);

        var bufferFactory = new DefaultDataBufferFactory();
        DefaultDataBuffer firstChunkBuffer = bufferFactory.wrap(firstChunk);
        DefaultDataBuffer secondChunkBuffer = bufferFactory.wrap(secondChunk);

        Flux<DataBuffer> innerFlux = Flux.concat(
                Flux.just(firstChunkBuffer),
                Flux.defer(() -> {
                            attemptsTracker.incrementAndGet();
                            Throwable exception = exceptionQueue.poll();

                            if (exception != null) {
                                return Flux.error(exception);
                            }
                            return Flux.just(secondChunkBuffer);
                        }
                )
        );
        return new TestFlux<>(innerFlux, SOME_DATA, attemptsTracker);
    }

    public static TestFlux<DataBuffer> immediatelyFailingThenSucceeding(List<Throwable> exceptions) {
        byte[] bytes = SOME_DATA.getBytes();
        DefaultDataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(bytes);
        Deque<Throwable> exceptionQueue = new ArrayDeque<>(exceptions);
        AtomicInteger attemptsTracker = new AtomicInteger(0);

        Flux<DataBuffer> innerFlux = Flux.defer(() -> {
            attemptsTracker.incrementAndGet();
            Throwable exception = exceptionQueue.poll();

            if (exception != null) {
                return Flux.error(exception);
            }

            return Flux.just(dataBuffer);
        });
        return new TestFlux<>(innerFlux, SOME_DATA, attemptsTracker);
    }

    public static TestFlux<DataBuffer> immediatelyFailing(Throwable exception) {
        var attemptsTracker = new AtomicInteger(0);
        Flux<DataBuffer> innerFlux = Flux.defer(() -> {
            attemptsTracker.incrementAndGet();
            return Flux.error(exception);
        });
        return new TestFlux<>(innerFlux, SOME_DATA, attemptsTracker);
    }
}
