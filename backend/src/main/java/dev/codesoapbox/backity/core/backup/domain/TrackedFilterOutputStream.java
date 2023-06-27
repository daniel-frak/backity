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
    public void write(byte[] currentByte, int offset, int length) throws IOException {
        out.write(currentByte, offset, length);
        backupProgress.incrementDownloadedBytes(length);
    }

    @Override
    public void write(int currentByte) throws IOException {
        out.write(currentByte);
        backupProgress.incrementProcessedElements();
    }

    @Override
    public void close() throws IOException {
        super.close();
        backupProgress.updateContentLength();
    }
}
