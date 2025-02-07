package dev.codesoapbox.backity.core.backup.application.downloadprogress;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class TrackedFilterOutputStream extends FilterOutputStream {

    private final ProgressTracker progressTracker;

    public TrackedFilterOutputStream(ProgressTracker progressTracker, OutputStream fileOutputStream) {
        super(fileOutputStream);
        this.progressTracker = progressTracker;
    }

    @Override
    public void write(byte[] bytesToWrite, int offset, int length) throws IOException {
        out.write(bytesToWrite, offset, length);
        progressTracker.incrementDownloadedBytes(length);
    }

    @Override
    public void write(int byteToWrite) throws IOException {
        out.write(byteToWrite);
        progressTracker.incrementProcessedElements();
    }

    @Override
    public void close() throws IOException {
        super.close();
        progressTracker.updateContentLength();
    }
}
