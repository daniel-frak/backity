package dev.codesoapbox.backity.core.backup.domain;

import dev.codesoapbox.backity.core.discovery.domain.ProgressInfo;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class TrackedFilterOutputStream extends FilterOutputStream {

    private final BackupProgress backupProgress;

    public TrackedFilterOutputStream(BackupProgress backupProgress, OutputStream fileOutputStream) {
        super(fileOutputStream);
        this.backupProgress = backupProgress;
    }

    @Override
    public void write(byte[] currentByte, int offset, int length) throws IOException {
        out.write(currentByte, offset, length);
        backupProgress.downloadedLengthBytes += length;
        backupProgress.progressTracker.incrementBy(length);
        updateProgress(backupProgress.progressTracker.getProgressInfo());
    }

    private void updateProgress(ProgressInfo progressInfo) {
        backupProgress.progressConsumers.forEach(c -> c.accept(progressInfo));
    }

    @Override
    public void write(int currentByte) throws IOException {
        out.write(currentByte);
        backupProgress.downloadedLengthBytes++;
        backupProgress.progressTracker.incrementBy(1);
        updateProgress(backupProgress.progressTracker.getProgressInfo());
    }

    @Override
    public void close() throws IOException {
        super.close();
        done();
    }

    private void done() {
        if (backupProgress.contentLengthBytes == -1) {
            backupProgress.contentLengthBytes = backupProgress.downloadedLengthBytes;
        }
    }
}
