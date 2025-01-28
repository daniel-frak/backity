package dev.codesoapbox.backity.core.backup.domain;

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
    public void write(byte[] bytesToWrite, int offset, int length) throws IOException {
        out.write(bytesToWrite, offset, length);
        backupProgress.incrementDownloadedBytes(length);
    }

    @Override
    public void write(int byteToWrite) throws IOException {
        out.write(byteToWrite);
        backupProgress.incrementProcessedElements();
    }

    @Override
    public void close() throws IOException {
        super.close();
        backupProgress.updateContentLength();
    }
}
