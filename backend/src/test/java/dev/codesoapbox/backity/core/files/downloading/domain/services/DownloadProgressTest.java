package dev.codesoapbox.backity.core.files.downloading.domain.services;

import dev.codesoapbox.backity.core.files.discovery.domain.model.ProgressInfo;
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

        OutputStream outputStream = downloadProgress.getTrackedOutputStream(new ByteArrayOutputStream(4));
        downloadProgress.startTracking(4);

        outputStream.write(new byte[]{0, 1}, 0, 2);

        assertEquals(2, downloadProgress.getDownloadedLengthBytes());
        assertEquals(50, downloadProgress.getProgressInfo().percentage());
    }

    @Test
    void shouldSubscribeToProgressAndUnsubscribeFromIt() throws IOException {
        var progressInfoReference = new AtomicReference<ProgressInfo>();
        Consumer<ProgressInfo> progressConsumer = progressInfoReference::set;

        var downloadProgress = new DownloadProgress();

        downloadProgress.subscribeToProgress(progressConsumer);
        OutputStream outputStream = downloadProgress.getTrackedOutputStream(new ByteArrayOutputStream(4));
        downloadProgress.startTracking(4);

        outputStream.write(new byte[]{0, 1}, 0, 2);
        downloadProgress.unsubscribeFromProgress(progressConsumer);
        outputStream.write(new byte[]{0, 1}, 0, 2);

        assertEquals(50, progressInfoReference.get().percentage());
    }
}