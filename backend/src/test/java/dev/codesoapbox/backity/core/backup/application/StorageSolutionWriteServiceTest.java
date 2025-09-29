package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.exceptions.ConcurrentFileWriteException;
import dev.codesoapbox.backity.core.backup.application.exceptions.StorageSolutionWriteFailedException;
import dev.codesoapbox.backity.core.backup.application.exceptions.FileWriteWasCanceledException;
import dev.codesoapbox.backity.core.storagesolution.domain.FakeUnixStorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

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

            @Test
            void shouldNotThrowGivenWritingToSameFilePathButDifferentStorageSolution() {
                var differentStorageSolution = new FakeUnixStorageSolution();
                differentStorageSolution.setId(new StorageSolutionId("DifferentStorageSolutionId"));
                var filePath = "testFilePath";
                var firstFileStream = new FakeTrackableFileStream();
                var secondFileStream = new FakeTrackableFileStream();
                firstFileStream.triggerOnWrite(
                        () -> storageSolutionWriteService.writeFileToStorage(
                                secondFileStream, differentStorageSolution, filePath));

                assertThatCode(() -> storageSolutionWriteService.writeFileToStorage(
                        firstFileStream, storageSolution, filePath))
                        .doesNotThrowAnyException();
            }
        }
    }

    @Nested
    class CancelWrite {

        @Test
        void shouldThrow() {
            var filePath = "testFilePath";
            var writeDestination = new WriteDestination(storageSolution.getId(), filePath);
            var fileStream = new FakeTrackableFileStream();
            fileStream.triggerOnWrite(() -> storageSolutionWriteService.cancelWrite(writeDestination));

            assertThatThrownBy(() -> storageSolutionWriteService.writeFileToStorage(
                    fileStream, storageSolution, filePath))
                    .isInstanceOf(FileWriteWasCanceledException.class);
        }

        @Test
        void shouldNotValidateSizeGivenWriteWasCanceled() {
            var filePath = "testFilePath";
            var writeDestination = new WriteDestination(storageSolution.getId(), filePath);
            var fileStream = new FakeTrackableFileStream();
            fileStream.triggerOnWrite(() -> storageSolutionWriteService.cancelWrite(writeDestination));

            storageSolution.overrideWrittenSizeFor(filePath, smallerThanExpectedSizeInBytes(fileStream));

            assertThatThrownBy(() -> storageSolutionWriteService.writeFileToStorage(
                    fileStream, storageSolution, filePath))
                    .isInstanceOf(FileWriteWasCanceledException.class);
        }

        @Test
        void cancelWriteShouldNotThrowGivenFileIsNotCurrentlyBeingWritten() {
            var writeDestination = new WriteDestination(storageSolution.getId(), "nonExistentFilePath");
            assertThatCode(() -> storageSolutionWriteService.cancelWrite(writeDestination))
                    .doesNotThrowAnyException();
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void cancelWriteShouldThrowGivenNullWriteDestination() {
            assertThatThrownBy(() -> storageSolutionWriteService.cancelWrite(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("writeDestination");
        }
    }
}