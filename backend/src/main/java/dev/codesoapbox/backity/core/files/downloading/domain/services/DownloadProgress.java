package dev.codesoapbox.backity.core.files.downloading.domain.services;

import dev.codesoapbox.backity.core.files.discovery.domain.model.IncrementalProgressTracker;
import dev.codesoapbox.backity.core.files.discovery.domain.model.ProgressInfo;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Tracks the progress of an OutputStream.
 * <p>
 * Based on:
 * https://stackoverflow.com/a/68971635/11247096
 */
public class DownloadProgress {

    private final List<Consumer<ProgressInfo>> progressConsumers = new ArrayList<>();
    private IncrementalProgressTracker progressTracker;

    private long contentLengthBytes = -1;
    private long downloadedLengthBytes;

    public void startTracking(long contentLengthBytes) {
        this.contentLengthBytes = contentLengthBytes;
        this.progressTracker = new IncrementalProgressTracker(contentLengthBytes, Clock.systemDefaultZone());
    }

    public OutputStream getTrackedOutputStream(OutputStream fileOutputStream) {
        return new FilterOutputStream(fileOutputStream) {

            @Override
            public void write(byte[] currentByte, int offset, int length) throws IOException {
                out.write(currentByte, offset, length);
                downloadedLengthBytes += length;
                progressTracker.incrementBy(length);
                updateProgress(progressTracker.getProgressInfo());
            }

            @Override
            public void write(int currentByte) throws IOException {
                out.write(currentByte);
                downloadedLengthBytes++;
            }

            @Override
            public void close() throws IOException {
                super.close();
                done();
            }
        };
    }

    private void updateProgress(ProgressInfo progressInfo) {
        progressConsumers.forEach(c -> c.accept(progressInfo));
    }

    private void done() {
        if (contentLengthBytes == -1) {
            contentLengthBytes = downloadedLengthBytes;
        } else {
            downloadedLengthBytes = contentLengthBytes;
        }
    }

    public long getDownloadedLengthBytes() {
        return downloadedLengthBytes;
    }

    public ProgressInfo getProgressInfo() {
        return progressTracker.getProgressInfo();
    }

    public void subscribeToProgress(Consumer<ProgressInfo> progressConsumer) {
        progressConsumers.add(progressConsumer);
    }

    public void unsubscribeFromProgress(Consumer<ProgressInfo> progressConsumer) {
        progressConsumers.remove(progressConsumer);
    }
}
