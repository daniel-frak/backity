package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.exceptions.ConcurrentFileDownloadException;
import dev.codesoapbox.backity.core.backup.application.exceptions.FileDownloadFailedException;
import dev.codesoapbox.backity.core.backup.application.exceptions.FileDownloadWasCanceledException;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.core.storagesolution.domain.FakeUnixStorageSolution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DownloadServiceTest {

    private DownloadService downloadService;
    private FakeUnixStorageSolution storageSolution;

    @BeforeEach
    void setUp() {
        storageSolution = new FakeUnixStorageSolution();
        downloadService = new DownloadService();
    }

    private long smallerThanExpectedSizeInBytes(FakeTrackableFileStream fileStream) {
        return fileStream.data().length() - 1L;
    }

    @Nested
    class DownloadFile {

        @Nested
        class Successful {

            @Test
            void shouldDownloadFileToStorageSolution() {
                GameFile gameFile = TestGameFile.gog();
                var filePath = "testFilePath";
                var fileStream = new FakeTrackableFileStream();

                downloadService.downloadFile(storageSolution, fileStream, gameFile, filePath);

                assertThat(storageSolution.fileExists(filePath)).isTrue();
            }
        }

        @Nested
        class Throwing {

            @Test
            void shouldThrowGivenFileSizeDoesNotMatch() {
                var filePath = "someFilePath";
                GameFile gameFile = TestGameFile.gog();
                var fileStream = new FakeTrackableFileStream();
                long writtenSizeInBytes = smallerThanExpectedSizeInBytes(fileStream);
                storageSolution.overrideWrittenSizeFor(filePath, writtenSizeInBytes);

                assertThatThrownBy(() -> downloadService.downloadFile(storageSolution, fileStream, gameFile, filePath))
                        .isInstanceOf(FileDownloadFailedException.class)
                        .message()
                        .isEqualTo(String.format(
                                "The downloaded size of someFilePath is not what was expected (was %s, expected %s)",
                                writtenSizeInBytes, fileStream.data().length()));
            }

            @Test
            void shouldThrowGivenAlreadyDownloading() {
                GameFile gameFile = TestGameFile.gog();
                var filePath = "testFilePath";
                var fileStream = new FakeTrackableFileStream();
                fileStream.triggerOnWrite(
                        () -> downloadService.downloadFile(storageSolution, fileStream, gameFile, filePath));

                assertThatThrownBy(() -> downloadService.downloadFile(storageSolution, fileStream, gameFile, filePath))
                        .isInstanceOf(ConcurrentFileDownloadException.class);
            }
        }
    }

    @Nested
    class CancelDownload {

        @Test
        void shouldCancelDownload() {
            GameFile gameFile = TestGameFile.gog();
            var filePath = "testFilePath";
            var fileStream = new FakeTrackableFileStream();
            fileStream.triggerOnWrite(() -> downloadService.cancelDownload(filePath));

            assertThatThrownBy(() -> downloadService.downloadFile(storageSolution, fileStream, gameFile, filePath))
                    .isInstanceOf(FileDownloadWasCanceledException.class);
        }

        @Test
        void shouldNotValidateSizeGivenDownloadWasCanceled() {
            GameFile gameFile = TestGameFile.gog();
            var filePath = "testFilePath";
            var fileStream = new FakeTrackableFileStream();
            fileStream.triggerOnWrite(() -> downloadService.cancelDownload(filePath));

            storageSolution.overrideWrittenSizeFor(filePath, smallerThanExpectedSizeInBytes(fileStream));

            assertThatThrownBy(() -> downloadService.downloadFile(storageSolution, fileStream, gameFile, filePath))
                    .isInstanceOf(FileDownloadWasCanceledException.class);
        }

        @Test
        void cancelDownloadShouldNotThrowGivenFileIsNotCurrentlyBeingDownloaded() {
            assertThatCode(() -> downloadService.cancelDownload("nonExistentFilePath"))
                    .doesNotThrowAnyException();
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void cancelDownloadShouldThrowGivenNullFilePath() {
            assertThatThrownBy(() -> downloadService.cancelDownload(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("filePath");
        }
    }
}