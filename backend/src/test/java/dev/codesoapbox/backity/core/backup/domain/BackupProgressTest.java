package dev.codesoapbox.backity.core.backup.domain;

import dev.codesoapbox.backity.core.discovery.domain.ProgressInfo;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BackupProgressTest {

    @Test
    void shouldTrackOutputStream() throws IOException {
        var downloadProgress = new BackupProgress();

        try (OutputStream outputStream = downloadProgress.getTrackedOutputStream(new ByteArrayOutputStream(4))) {
            downloadProgress.startTracking(4);

            outputStream.write(new byte[]{0}, 0, 1);
            outputStream.write(1);
        }

        assertEquals(2, downloadProgress.getDownloadedLengthBytes());
        assertEquals(50, downloadProgress.getProgressInfo().percentage());
        assertEquals(4, downloadProgress.getContentLengthBytes());
    }

    @Test
    void getTrackedOutputStreamShouldSetContentLengthAfterDownloading() throws IOException {
        var downloadProgress = new BackupProgress();

        try (OutputStream outputStream = downloadProgress.getTrackedOutputStream(new ByteArrayOutputStream(4))) {
            downloadProgress.startTracking(-1);

            outputStream.write(new byte[]{0}, 0, 1);
            outputStream.write(1);
        }
        assertEquals(2, downloadProgress.getContentLengthBytes());
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
        assertEquals(25, progressInfoReference.get().percentage());

        outputStream.write(new byte[]{0, 1}, 0, 2);
        downloadProgress.unsubscribeFromProgress(progressConsumer);
        outputStream.write(new byte[]{0, 1}, 0, 2);

        assertEquals(75, progressInfoReference.get().percentage());
    }
}