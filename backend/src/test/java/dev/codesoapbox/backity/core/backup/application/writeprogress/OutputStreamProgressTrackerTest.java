package dev.codesoapbox.backity.core.backup.application.writeprogress;

import dev.codesoapbox.backity.shared.application.progress.ProgressInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Clock;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class OutputStreamProgressTrackerTest {

    @Mock
    private Clock clock;

    @Test
    void shouldTrackOutputStream() throws IOException {
        var outputStreamProgressTracker = new OutputStreamProgressTracker();
        var currentPercentage = new AtomicInteger();
        outputStreamProgressTracker.subscribeToProgress(
                progressInfo -> currentPercentage.set(progressInfo.percentage()));

        try (OutputStream outputStream = outputStreamProgressTracker.track(new ByteArrayOutputStream(4))) {
            outputStreamProgressTracker.initializeTracking(4, clock);

            outputStream.write(new byte[]{0}, 0, 1);
            outputStream.write(1);
        }

        assertThat(outputStreamProgressTracker.getWrittenBytesLength()).isEqualTo(2);
        assertThat(currentPercentage.get()).isEqualTo(50);
        assertThat(outputStreamProgressTracker.getContentLengthBytes()).isEqualTo(4);
    }

    @Test
    void trackShouldSetContentLengthAfterWriting() throws IOException {
        var outputStreamProgressTracker = new OutputStreamProgressTracker();

        try (OutputStream outputStream = outputStreamProgressTracker.track(new ByteArrayOutputStream(4))) {
            outputStreamProgressTracker.initializeTracking(-1, clock);
            outputStream.write(new byte[]{0}, 0, 1);
            outputStream.write(1);
        }

        assertThat(outputStreamProgressTracker.getContentLengthBytes()).isEqualTo(2);
    }

    @Test
    void shouldSubscribeToProgress() throws IOException {
        var progressInfoReference = new AtomicReference<ProgressInfo>();
        Consumer<ProgressInfo> progressConsumer = progressInfoReference::set;

        var outputStreamProgressTracker = new OutputStreamProgressTracker();

        outputStreamProgressTracker.subscribeToProgress(progressConsumer);
        OutputStream outputStream = outputStreamProgressTracker.track(new ByteArrayOutputStream(4));
        outputStreamProgressTracker.initializeTracking(4, clock);

        outputStream.write(1);
        assertThat(progressInfoReference.get().percentage()).isEqualTo(25);

        outputStream.write(new byte[]{0, 1}, 0, 2);

        assertThat(progressInfoReference.get().percentage()).isEqualTo(75);
    }
}