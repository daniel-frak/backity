package dev.codesoapbox.backity.core.backup.application.downloadprogress;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.OutputStream;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TrackedFilterOutputStreamTest {

    private TrackedFilterOutputStream trackedFilterOutputStream;

    @Mock
    private BackupProgress backupProgress;

    @Mock
    private OutputStream outputStream;

    @BeforeEach
    void setUp() {
        trackedFilterOutputStream = new TrackedFilterOutputStream(backupProgress, outputStream);
    }

    @Test
    void writeWithLengthShouldWriteToOutputStream() throws IOException {
        trackedFilterOutputStream.write(new byte[]{1, 2}, 20, 30);

        verify(outputStream).write(new byte[]{1, 2}, 20, 30);
    }

    @Test
    void writeWithLengthShouldUpdateBackupProgress() throws IOException {
        trackedFilterOutputStream.write(new byte[]{1, 2}, 20, 30);

        verify(backupProgress).incrementDownloadedBytes(30);
    }

    @Test
    void writeShouldWriteToOutputStream() throws IOException {
        trackedFilterOutputStream.write(12);

        verify(outputStream).write(12);
    }

    @Test
    void writeShouldUpdateBackupProgress() throws IOException {
        trackedFilterOutputStream.write(12);

        verify(backupProgress).incrementProcessedElements();
    }

    @Test
    void closeShouldCloseStream() throws IOException {
        trackedFilterOutputStream.close();

        verify(outputStream).close();
    }

    @Test
    void closeShouldUpdateContentLength() throws IOException {
        trackedFilterOutputStream.close();

        verify(backupProgress).updateContentLength();
    }
}