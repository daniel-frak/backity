package dev.codesoapbox.backity.core.backup.application.writeprogress;

import dev.codesoapbox.backity.shared.application.progress.ProgressInfo;
import dev.codesoapbox.backity.shared.application.progress.ProgressTracker;
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
public class OutputStreamProgressTracker {

    protected final List<Consumer<ProgressInfo>> progressConsumers = new ArrayList<>();
    protected ProgressTracker progressTracker = null;

    @Getter
    protected long contentLengthBytes = -1;

    @Getter
    protected long writtenBytesLength = 0;

    public void initializeTracking(long contentLengthBytes, Clock clock) {
        this.contentLengthBytes = contentLengthBytes;
        this.progressTracker = new ProgressTracker(contentLengthBytes, clock);
    }

    public OutputStream track(OutputStream fileOutputStream) {
        var trackedFilterOutputStream = new TrackedFilterOutputStream(fileOutputStream);
        trackedFilterOutputStream.setOnWrite(this::incrementWrittenBytes);
        trackedFilterOutputStream.setOnClose(this::updateContentLength);

        return trackedFilterOutputStream;
    }

    public void subscribeToProgress(Consumer<ProgressInfo> progressConsumer) {
        progressConsumers.add(progressConsumer);
    }

    public void incrementWrittenBytes(int length) {
        writtenBytesLength += length;
        progressTracker.incrementBy(length);
        updateProgress();
    }

    private void updateProgress() {
        progressConsumers.forEach(c -> c.accept(progressTracker.getProgressInfo()));
    }

    public void updateContentLength() {
        if (contentLengthBytes == -1) {
            contentLengthBytes = writtenBytesLength;
        }
    }
}
