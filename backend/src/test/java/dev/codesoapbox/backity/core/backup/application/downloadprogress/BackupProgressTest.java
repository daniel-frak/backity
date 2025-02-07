package dev.codesoapbox.backity.core.backup.application.downloadprogress;

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
class BackupProgressTest {

    @Mock
    private Clock clock;

    @Test
    void shouldTrackOutputStream() throws IOException {
        var backupProgress = new BackupProgress();
        AtomicInteger currentPercentage = new AtomicInteger();
        backupProgress.subscribeToProgress(progressInfo -> currentPercentage.set(progressInfo.percentage()));

        try (OutputStream outputStream = backupProgress.track(new ByteArrayOutputStream(4))) {
            backupProgress.initializeTracking(4, clock);

            outputStream.write(new byte[]{0}, 0, 1);
            outputStream.write(1);
        }

        assertThat(backupProgress.getDownloadedLengthBytes()).isEqualTo(2);
        assertThat(currentPercentage.get()).isEqualTo(50);
        assertThat(backupProgress.getContentLengthBytes()).isEqualTo(4);
    }

    @Test
    void trackShouldSetContentLengthAfterDownloading() throws IOException {
        var backupProgress = new BackupProgress();

        try (OutputStream outputStream = backupProgress.track(new ByteArrayOutputStream(4))) {
            backupProgress.initializeTracking(-1, clock);
            outputStream.write(new byte[]{0}, 0, 1);
            outputStream.write(1);
        }

        assertThat(backupProgress.getContentLengthBytes()).isEqualTo(2);
    }

    @Test
    void shouldSubscribeToProgress() throws IOException {
        var progressInfoReference = new AtomicReference<ProgressInfo>();
        Consumer<ProgressInfo> progressConsumer = progressInfoReference::set;

        var backupProgress = new BackupProgress();

        backupProgress.subscribeToProgress(progressConsumer);
        OutputStream outputStream = backupProgress.track(new ByteArrayOutputStream(4));
        backupProgress.initializeTracking(4, clock);

        outputStream.write(1);
        assertThat(progressInfoReference.get().percentage()).isEqualTo(25);

        outputStream.write(new byte[]{0, 1}, 0, 2);

        assertThat(progressInfoReference.get().percentage()).isEqualTo(75);
    }
}