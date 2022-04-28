package dev.codesoapbox.backity.core.files.downloading.domain.services;

import dev.codesoapbox.backity.core.files.discovery.domain.model.IncrementalProgressTracker;
import dev.codesoapbox.backity.core.files.discovery.domain.model.ProgressInfo;

import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * https://stackoverflow.com/a/68971635/11247096
 */
public class DownloadProgress {

    private final List<Consumer<ProgressInfo>> progressConsumers = new ArrayList<>();
    private IncrementalProgressTracker progressTracker;

    private long contentLength = -1;
    private long downloaded;

    public void setLength(long contentLength) {
        this.contentLength = contentLength;
        this.progressTracker = new IncrementalProgressTracker(contentLength);
    }

    public OutputStream getOutputStream(FileOutputStream fileOutputStream) {
        return new FilterOutputStream(fileOutputStream) {

            @Override
            public void write(byte[] b, int off, int length) throws IOException {
                out.write(b, off, length);
                downloaded += length;
                progressTracker.incrementBy(length);
                updateProgress(progressTracker.getProgressInfo());
            }
            @Override
            public void write(int b) throws IOException {
                out.write(b);
                downloaded++;
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

    public void done() {
        if ( contentLength == -1 ) {
            contentLength = downloaded;
        } else {
            downloaded = contentLength;
        }
    }

    public double getProgress() {
        if ( contentLength == -1 ) return 0;
        return downloaded / (double) contentLength;
    }

    public long getDownloaded() {
        return downloaded;
    }

    public boolean finished() {
        return downloaded > 0 && downloaded == contentLength;
    }

    public void subscribeToProgress(Consumer<ProgressInfo> progressConsumer) {
        progressConsumers.add(progressConsumer);
    }

    public void unsubscribeFromProgress(Consumer<ProgressInfo> progressConsumer) {
        progressConsumers.remove(progressConsumer);
    }
}
