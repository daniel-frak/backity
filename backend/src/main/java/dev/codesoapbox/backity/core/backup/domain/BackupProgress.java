package dev.codesoapbox.backity.core.backup.domain;

import dev.codesoapbox.backity.core.discovery.domain.IncrementalProgressTracker;
import dev.codesoapbox.backity.core.discovery.domain.ProgressInfo;
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
public class BackupProgress {

    protected final List<Consumer<ProgressInfo>> progressConsumers = new ArrayList<>();
    protected IncrementalProgressTracker progressTracker = null;

    @Getter
    protected long contentLengthBytes = -1;
    protected long downloadedLengthBytes = 0;

    public void startTracking(long contentLengthBytes) {
        this.contentLengthBytes = contentLengthBytes;
        this.progressTracker = new IncrementalProgressTracker(contentLengthBytes, Clock.systemDefaultZone());
    }

    public OutputStream getTrackedOutputStream(OutputStream fileOutputStream) {
        return new TrackedFilterOutputStream(this, fileOutputStream);
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

    @SuppressWarnings("squid:S2250")
    public void unsubscribeFromProgress(Consumer<ProgressInfo> progressConsumer) {
        progressConsumers.remove(progressConsumer);
    }
}
