package dev.codesoapbox.backity.core.backup.application.writeprogress;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
    private OutputStreamProgressTracker outputStreamProgressTracker;

    @Mock
    private OutputStream outputStream;

    @BeforeEach
    void setUp() {
        trackedFilterOutputStream = new TrackedFilterOutputStream(outputStreamProgressTracker, outputStream);
    }

    @Nested
    class Writing {

        @Test
        void writeShouldWriteToOutputStreamGivenLength() throws IOException {
            trackedFilterOutputStream.write(new byte[]{1, 2}, 20, 30);

            verify(outputStream).write(new byte[]{1, 2}, 20, 30);
        }

        @Test
        void writeShouldUpdateOutputStreamProgressGivenLength() throws IOException {
            trackedFilterOutputStream.write(new byte[]{1, 2}, 20, 30);

            verify(outputStreamProgressTracker).incrementWrittenBytes(30);
        }

        @Test
        void writeShouldWriteToOutputStream() throws IOException {
            trackedFilterOutputStream.write(12);

            verify(outputStream).write(12);
        }

        @Test
        void writeShouldUpdateOutputStreamProgressGivenSingleByte() throws IOException {
            trackedFilterOutputStream.write(12);

            verify(outputStreamProgressTracker).incrementWrittenBytes(1);
        }
    }

    @Nested
    class Closing {

        @Test
        void closeShouldCloseStream() throws IOException {
            trackedFilterOutputStream.close();

            verify(outputStream).close();
        }

        @Test
        void closeShouldUpdateContentLength() throws IOException {
            trackedFilterOutputStream.close();

            verify(outputStreamProgressTracker).updateContentLength();
        }
    }
}