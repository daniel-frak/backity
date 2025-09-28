package dev.codesoapbox.backity.core.backup.application.writeprogress;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.OutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TrackedFilterOutputStreamTest {

    private TrackedFilterOutputStream trackedFilterOutputStream;

    private int writtenBytesLength;
    private boolean wasClosed;

    @Mock
    private OutputStream outputStream;

    @BeforeEach
    void setUp() {
        writtenBytesLength = 0;
        wasClosed = false;
        trackedFilterOutputStream = new TrackedFilterOutputStream(outputStream);
        trackedFilterOutputStream.setOnWrite(bytesLength -> writtenBytesLength += bytesLength);
        trackedFilterOutputStream.setOnClose(() -> wasClosed = true);
    }

    @Nested
    class Writing {

        @Test
        void writeShouldWriteToOutputStreamGivenLength() throws IOException {
            trackedFilterOutputStream.write(new byte[]{1, 2}, 20, 30);

            verify(outputStream).write(new byte[]{1, 2}, 20, 30);
        }

        @Test
        void writeShouldNotifySubscriberGivenLength() throws IOException {
            trackedFilterOutputStream.write(new byte[]{1, 2}, 20, 30);

            assertThat(writtenBytesLength).isEqualTo(30);
        }

        @Test
        void writeShouldWriteToOutputStream() throws IOException {
            trackedFilterOutputStream.write(12);

            verify(outputStream).write(12);
        }

        @Test
        void writeShouldNotifySubscriberGivenSingleByte() throws IOException {
            trackedFilterOutputStream.write(12);

            assertThat(writtenBytesLength).isEqualTo(1);
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
        void closeShouldNotifySubscriber() throws IOException {
            trackedFilterOutputStream.close();

            assertThat(wasClosed).isTrue();
        }
    }
}