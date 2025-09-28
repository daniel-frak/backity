package dev.codesoapbox.backity.core.backup.application.writeprogress;

import lombok.Setter;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;

class TrackedFilterOutputStream extends FilterOutputStream {

    @Setter
    private Consumer<Integer> onWrite;

    @Setter
    private Runnable onClose;

    public TrackedFilterOutputStream(OutputStream fileOutputStream) {
        super(fileOutputStream);
    }

    @Override
    public void write(byte[] bytesToWrite, int offset, int length) throws IOException {
        out.write(bytesToWrite, offset, length);
        onWrite.accept(length);
    }

    @Override
    public void write(int byteToWrite) throws IOException {
        out.write(byteToWrite);
        onWrite.accept(1);
    }

    @Override
    public void close() throws IOException {
        super.close();
        onClose.run();
    }
}
