package dev.codesoapbox.backity.core.files.domain.downloading.services;

import dev.codesoapbox.backity.core.files.domain.discovery.model.ProgressInfo;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DownloadProgressTest {

    @Test
    void shouldTrackOutputStream() throws IOException {
        var downloadProgress = new DownloadProgress();

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
        var downloadProgress = new DownloadProgress();

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

        var downloadProgress = new DownloadProgress();

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