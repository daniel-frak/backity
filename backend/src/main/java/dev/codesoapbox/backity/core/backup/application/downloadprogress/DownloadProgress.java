package dev.codesoapbox.backity.core.backup.application.downloadprogress;

import lombok.Getter;

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
public class DownloadProgress implements ProgressTracker {

    protected final List<Consumer<ProgressInfo>> progressConsumers = new ArrayList<>();
    protected IncrementalProgressTracker progressTracker = null;

    @Getter
    protected long contentLengthBytes = -1;

    @Getter
    protected long downloadedLengthBytes = 0;

    public void initializeTracking(long contentLengthBytes, Clock clock) {
        this.contentLengthBytes = contentLengthBytes;
        this.progressTracker = new IncrementalProgressTracker(contentLengthBytes, clock);
    }

    public OutputStream track(OutputStream fileOutputStream) {
        return new TrackedFilterOutputStream(this, fileOutputStream);
    }

    public void subscribeToProgress(Consumer<ProgressInfo> progressConsumer) {
        progressConsumers.add(progressConsumer);
    }

    @Override
    public void incrementDownloadedBytes(int length) {
        downloadedLengthBytes += length;
        progressTracker.incrementBy(length);
        updateProgress();
    }

    private void updateProgress() {
        progressConsumers.forEach(c -> c.accept(progressTracker.getProgressInfo()));
    }

    @Override
    public void incrementProcessedElements() {
        downloadedLengthBytes++;
        progressTracker.incrementBy(1);
        updateProgress();
    }

    @Override
    public void updateContentLength() {
        if (contentLengthBytes == -1) {
            contentLengthBytes = downloadedLengthBytes;
        }
    }
}
