package dev.codesoapbox.backity.core.backup.domain;

import dev.codesoapbox.backity.core.discovery.domain.ProgressInfo;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

class BackupProgressTest {

    @Test
    void shouldTrackOutputStream() throws IOException {
        var downloadProgress = new BackupProgress();

        try (OutputStream outputStream = downloadProgress.getTrackedOutputStream(new ByteArrayOutputStream(4))) {
            downloadProgress.startTracking(4);

            outputStream.write(new byte[]{0}, 0, 1);
            outputStream.write(1);
        }

        assertThat(downloadProgress.getDownloadedLengthBytes()).isEqualTo(2);
        assertThat(downloadProgress.getProgressInfo().percentage()).isEqualTo(50);
        assertThat(downloadProgress.getContentLengthBytes()).isEqualTo(4);
    }

    @Test
    void getTrackedOutputStreamShouldSetContentLengthAfterDownloading() throws IOException {
        var downloadProgress = new BackupProgress();

        try (OutputStream outputStream = downloadProgress.getTrackedOutputStream(new ByteArrayOutputStream(4))) {
            downloadProgress.startTracking(-1);
            outputStream.write(new byte[]{0}, 0, 1);
            outputStream.write(1);
        }

        assertThat(downloadProgress.getContentLengthBytes()).isEqualTo(2);
    }

    @Test
    void shouldSubscribeToProgressAndUnsubscribeFromIt() throws IOException {
        var progressInfoReference = new AtomicReference<ProgressInfo>();
        Consumer<ProgressInfo> progressConsumer = progressInfoReference::set;

        var downloadProgress = new BackupProgress();

        downloadProgress.subscribeToProgress(progressConsumer);
        OutputStream outputStream = downloadProgress.getTrackedOutputStream(new ByteArrayOutputStream(4));
        downloadProgress.startTracking(4);

        outputStream.write(1);
        assertThat(progressInfoReference.get().percentage()).isEqualTo(25);

        outputStream.write(new byte[]{0, 1}, 0, 2);
        downloadProgress.unsubscribeFromProgress(progressConsumer);
        outputStream.write(new byte[]{0, 1}, 0, 2);

        assertThat(progressInfoReference.get().percentage()).isEqualTo(75);
    }
}