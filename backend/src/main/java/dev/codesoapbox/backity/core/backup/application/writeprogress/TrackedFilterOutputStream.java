package dev.codesoapbox.backity.core.backup.application.writeprogress;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class TrackedFilterOutputStream extends FilterOutputStream {

    private final OutputStreamProgressTracker outputStreamProgressTracker;

    public TrackedFilterOutputStream(
            OutputStreamProgressTracker outputStreamProgressTracker, OutputStream fileOutputStream) {
        super(fileOutputStream);
        this.outputStreamProgressTracker = outputStreamProgressTracker;
    }

    @Override
    public void write(byte[] bytesToWrite, int offset, int length) throws IOException {
        out.write(bytesToWrite, offset, length);
        outputStreamProgressTracker.incrementWrittenBytes(length);
    }

    @Override
    public void write(int byteToWrite) throws IOException {
        out.write(byteToWrite);
        outputStreamProgressTracker.incrementWrittenBytes(1);
    }

    @Override
    public void close() throws IOException {
        super.close();
        outputStreamProgressTracker.updateContentLength();
    }
}
