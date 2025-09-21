package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.exceptions.ConcurrentFileWriteException;
import dev.codesoapbox.backity.core.backup.application.exceptions.StorageSolutionWriteFailedException;
import dev.codesoapbox.backity.core.backup.application.exceptions.FileWriteWasCanceledException;
import dev.codesoapbox.backity.core.storagesolution.domain.FakeUnixStorageSolution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StorageSolutionWriteServiceTest {

    private StorageSolutionWriteService storageSolutionWriteService;
    private FakeUnixStorageSolution storageSolution;

    @BeforeEach
    void setUp() {
        storageSolution = new FakeUnixStorageSolution();
        storageSolutionWriteService = new StorageSolutionWriteService();
    }

    private long smallerThanExpectedSizeInBytes(FakeTrackableFileStream fileStream) {
        return fileStream.data().length() - 1L;
    }

    @Nested
    class WriteFile {

        @Nested
        class Successful {

            @Test
            void shouldWriteFileToStorageSolution() {
                var filePath = "testFilePath";
                var fileStream = new FakeTrackableFileStream();

                storageSolutionWriteService.writeFileToStorage(fileStream, storageSolution, filePath);

                assertThat(storageSolution.fileExists(filePath)).isTrue();
            }
        }

        @Nested
        class Throwing {

            @Test
            void shouldThrowGivenFileSizeDoesNotMatch() {
                var filePath = "someFilePath";
                var fileStream = new FakeTrackableFileStream();
                long writtenSizeInBytes = smallerThanExpectedSizeInBytes(fileStream);
                storageSolution.overrideWrittenSizeFor(filePath, writtenSizeInBytes);

                assertThatThrownBy(() -> storageSolutionWriteService.writeFileToStorage(
                        fileStream, storageSolution, filePath))
                        .isInstanceOf(StorageSolutionWriteFailedException.class)
                        .message()
                        .isEqualTo(String.format(
                                "The written size of someFilePath is not what was expected (was %s, expected %s)",
                                writtenSizeInBytes, fileStream.data().length()));
            }

            @Test
            void shouldThrowGivenAlreadyWritingToSameFile() {
                var filePath = "testFilePath";
                var fileStream = new FakeTrackableFileStream();
                fileStream.triggerOnWrite(
                        () -> storageSolutionWriteService.writeFileToStorage(fileStream, storageSolution, filePath));

                assertThatThrownBy(() -> storageSolutionWriteService.writeFileToStorage(
                        fileStream, storageSolution, filePath))
                        .isInstanceOf(ConcurrentFileWriteException.class);
            }
        }
    }

    @Nested
    class CancelWrite {

        @Test
        void shouldThrow() {
            var filePath = "testFilePath";
            var fileStream = new FakeTrackableFileStream();
            fileStream.triggerOnWrite(() -> storageSolutionWriteService.cancelWrite(filePath));

            assertThatThrownBy(() -> storageSolutionWriteService.writeFileToStorage(
                    fileStream, storageSolution, filePath))
                    .isInstanceOf(FileWriteWasCanceledException.class);
        }

        @Test
        void shouldNotValidateSizeGivenWriteWasCanceled() {
            var filePath = "testFilePath";
            var fileStream = new FakeTrackableFileStream();
            fileStream.triggerOnWrite(() -> storageSolutionWriteService.cancelWrite(filePath));

            storageSolution.overrideWrittenSizeFor(filePath, smallerThanExpectedSizeInBytes(fileStream));

            assertThatThrownBy(() -> storageSolutionWriteService.writeFileToStorage(fileStream, storageSolution, filePath))
                    .isInstanceOf(FileWriteWasCanceledException.class);
        }

        @Test
        void cancelWriteShouldNotThrowGivenFileIsNotCurrentlyBeingWritten() {
            assertThatCode(() -> storageSolutionWriteService.cancelWrite("nonExistentFilePath"))
                    .doesNotThrowAnyException();
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void cancelWriteShouldThrowGivenNullFilePath() {
            assertThatThrownBy(() -> storageSolutionWriteService.cancelWrite(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("filePath");
        }
    }
}