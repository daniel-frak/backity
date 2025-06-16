package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgress;
import dev.codesoapbox.backity.core.backup.application.exceptions.FileDownloadException;
import dev.codesoapbox.backity.core.backup.application.exceptions.FileDownloadWasCanceledException;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.core.storagesolution.domain.FakeUnixStorageSolution;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.embed.testing.FakeTrackableFileStreamFactory;
import org.junit.function.ThrowingRunnable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Clock;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DownloadServiceTest {

    private DownloadService downloadService;
    private FakeUnixStorageSolution storageSolution;
    private FakeTrackableFileStreamFactory fileStreamFactory;
    private ThrowingRunnable onFileDownloadStarted;

    @Mock
    private Clock clock;

    @BeforeEach
    void setUp() {
        storageSolution = new FakeUnixStorageSolution();
        fileStreamFactory = new FakeTrackableFileStreamFactory(clock);
        downloadService = new DownloadService();
        onFileDownloadStarted = null;
    }

    @Test
    void downloadFileShouldDownloadToDisk() throws IOException {
        GameFile gameFile = TestGameFile.gog();
        String filePath = "testFilePath";
        String testData = "Test data";
        DownloadProgress downloadProgress = mockDownloadProgress(testData.length());
        TrackableFileStream fileStream = fileStreamFactory.create(downloadProgress, testData);

        downloadService.downloadFile(storageSolution, fileStream, gameFile, filePath);

        assertThat(storageSolution.fileExists(filePath)).isTrue();
        assertThat(storageSolution.getSizeInBytes(filePath)).isEqualTo(testData.length());
    }

    private DownloadProgress mockDownloadProgress(long contentLengthBytes) {
        DownloadProgress downloadProgress = mock(DownloadProgress.class);
        lenient().when(downloadProgress.track(any()))
                .thenAnswer(inv -> {
                    if (onFileDownloadStarted != null) {
                        onFileDownloadStarted.run();
                    }
                    return inv.getArgument(0);
                });
        lenient().when(downloadProgress.getContentLengthBytes())
                .thenReturn(contentLengthBytes);

        return downloadProgress;
    }

    @Test
    void downloadFileShouldTrackProgress() throws IOException {
        String testData = "Test data";
        DownloadProgress downloadProgress = mockDownloadProgress(testData.length());
        GameFile gameFile = TestGameFile.gog();
        TrackableFileStream fileStream = fileStreamFactory.create(downloadProgress, testData);

        downloadService.downloadFile(storageSolution, fileStream, gameFile, "someFilePath");

        InOrder inOrder = Mockito.inOrder(downloadProgress);
        inOrder.verify(downloadProgress).initializeTracking(9, clock);
        inOrder.verify(downloadProgress).track(any());
    }

    @Test
    void downloadFileShouldThrowGivenFileSizeDoesNotMatch() {
        String filePath = "someFilePath";
        GameFile gameFile = TestGameFile.gog();
        storageSolution.overrideDownloadedSizeFor(filePath, 999L);
        String testData = "Test data";
        DownloadProgress downloadProgress = mockDownloadProgress(testData.length());
        TrackableFileStream fileStream = fileStreamFactory.create(downloadProgress, testData);

        assertThatThrownBy(() -> downloadService.downloadFile(storageSolution, fileStream, gameFile, filePath))
                .isInstanceOf(FileDownloadException.class)
                .message()
                .isEqualTo(
                        "The downloaded size of someFilePath is not what was expected (was 999, expected 9)");
    }

    @Test
    void downloadFileShouldThrowGivenAlreadyDownloading() {
        GameFile gameFile = TestGameFile.gog();
        String filePath = "testFilePath";
        String testData = "test data";
        DownloadProgress downloadProgress = mockDownloadProgress(testData.length());
        TrackableFileStream fileStream = fileStreamFactory.create(downloadProgress, testData);
        onFileDownloadStarted = () -> downloadService.downloadFile(storageSolution, fileStream, gameFile, filePath);

        assertThatThrownBy(() -> downloadService.downloadFile(storageSolution, fileStream, gameFile, filePath))
                .isInstanceOf(FileDownloadException.class)
                .hasMessage("File 'testFilePath' is currently being downloaded by another thread");
    }

    @Test
    void shouldCancelDownload() {
        GameFile gameFile = TestGameFile.gog();
        String filePath = "testFilePath";
        String testData = "Test data";
        DownloadProgress downloadProgress = mockDownloadProgress(testData.length());
        TrackableFileStream fileStream = fileStreamFactory.create(downloadProgress, testData);

        onFileDownloadStarted = () -> downloadService.cancelDownload(filePath);

        assertThatThrownBy(() -> downloadService.downloadFile(storageSolution, fileStream, gameFile, filePath))
                .isInstanceOf(FileDownloadWasCanceledException.class);
        assertThat(storageSolution.fileExists(filePath)).isFalse();
    }

    @Test
    void shouldNotValidateSizeGivenDownloadWasCanceled() {
        GameFile gameFile = TestGameFile.gog();
        String filePath = "testFilePath";
        String testData = "Test data";
        DownloadProgress downloadProgress = mockDownloadProgress(testData.length());
        TrackableFileStream fileStream = fileStreamFactory.create(downloadProgress, testData);
        storageSolution.setCustomWrittenSizeInBytes(testData.length() + 1L); // Bigger downloaded than expected size
        onFileDownloadStarted = () -> downloadService.cancelDownload(filePath);

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