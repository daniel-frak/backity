package dev.codesoapbox.backity.gameproviders.shared.infrastructure.adapters.driven.api.spring;

import dev.codesoapbox.backity.core.backup.application.writeprogress.OutputStreamProgressTracker;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class DataBufferFluxTrackableFileStreamFactoryTest {

    @Test
    void shouldCreate() {
        int maxRetryAttempts = 3;
        Duration retryBackoff = Duration.ofSeconds(2);
        var factory = new DataBufferFluxTrackableFileStreamFactory(maxRetryAttempts, retryBackoff);
        Flux<DataBuffer> dataBufferFlux = Flux.just(mock(DataBuffer.class));
        OutputStreamProgressTracker progress = mock(OutputStreamProgressTracker.class);

        DataBufferFluxTrackableFileStream result = factory.create(dataBufferFlux, progress);

        var expectedFileStream = new DataBufferFluxTrackableFileStream(
                dataBufferFlux, progress, maxRetryAttempts, retryBackoff);
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(expectedFileStream);
    }
}