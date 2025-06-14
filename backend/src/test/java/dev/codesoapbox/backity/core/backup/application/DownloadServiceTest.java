package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgress;
import dev.codesoapbox.backity.core.storagesolution.domain.FakeUnixStorageSolution;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.core.backup.application.exceptions.FileDownloadException;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.embed.testing.FakeProgressAwareFileStreamFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Clock;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DownloadServiceTest {

    private DownloadService downloadService;
    private FakeUnixStorageSolution storageSolution;
    private FakeProgressAwareFileStreamFactory fileStreamFactory;

    @Mock
    private Clock clock;

    @BeforeEach
    void setUp() {
        storageSolution = new FakeUnixStorageSolution();
        fileStreamFactory = new FakeProgressAwareFileStreamFactory(clock);
        downloadService = new DownloadService();
    }

    @Test
    void downloadFileShouldDownloadToDisk() throws IOException {
        GameFile gameFile = TestGameFile.gog();
        String filePath = "testFilePath";
        DownloadProgress downloadProgress = mockDownloadProgress();
        TrackableFileStream fileStream = fileStreamFactory.create(downloadProgress, "Test data");

        downloadService.downloadFile(storageSolution, fileStream, gameFile, filePath);

        assertThat(storageSolution.fileExists(filePath)).isTrue();
    }

    private DownloadProgress mockDownloadProgress() {
        DownloadProgress downloadProgress = mock(DownloadProgress.class);
        lenient().when(downloadProgress.track(any()))
                .thenAnswer(inv -> inv.getArgument(0));
        lenient().when(downloadProgress.getContentLengthBytes())
                .thenReturn(9L);

        return downloadProgress;
    }

    @Test
    void downloadFileShouldTrackProgress() throws IOException {
        DownloadProgress downloadProgress = mockDownloadProgress();
        storageSolution = new FakeUnixStorageSolution();
        fileStreamFactory = new FakeProgressAwareFileStreamFactory(clock);
        when(downloadProgress.track(any()))
                .thenAnswer(inv -> inv.getArgument(0));
        when(downloadProgress.getContentLengthBytes())
                .thenReturn(9L);
        GameFile gameFile = TestGameFile.gog();
        TrackableFileStream fileStream = fileStreamFactory.create(downloadProgress, "Test data");

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
        DownloadProgress downloadProgress = mockDownloadProgress();
        TrackableFileStream fileStream = fileStreamFactory.create(downloadProgress, "Test data");

        assertThatThrownBy(() -> downloadService.downloadFile(storageSolution, fileStream, gameFile, filePath))
                .isInstanceOf(FileDownloadException.class)
                .message()
                .isEqualTo(
                        "The downloaded size of someFilePath is not what was expected (was 999, expected 9)");
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void downloadFileShouldThrowGivenAlreadyDownloading() {
        GameFile gameFile = TestGameFile.gog();
        String filePath = "testFilePath";
        DownloadProgress downloadProgress = mockDownloadProgress();
        AtomicBoolean shouldStop = new AtomicBoolean(false);
        try {
            TrackableFileStream fileStream = fileStreamFactory.createInfiniteStream(downloadProgress, shouldStop);

            startDownloadingInSeparateThread(fileStream, gameFile, filePath).start();

            assertThatThrownBy(() -> downloadService.downloadFile(storageSolution, fileStream, gameFile, filePath))
                    .isInstanceOf(FileDownloadException.class)
                    .hasMessage("File 'testFilePath' is currently being downloaded by another thread");
        } finally {
            shouldStop.set(true);
        }
    }

    private Thread startDownloadingInSeparateThread(TrackableFileStream fileStream, GameFile gameFile, String filePath) {
        return new Thread(() -> {
            try {
                downloadService.downloadFile(storageSolution, fileStream, gameFile, filePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void shouldCancelDownload() throws InterruptedException {
        GameFile gameFile = TestGameFile.gog();
        String filePath = "testFilePath";
        DownloadProgress downloadProgress = mockDownloadProgress();
        var shouldStop = new AtomicBoolean(false);
        try {
            TrackableFileStream fileStream = fileStreamFactory.createInfiniteStream(downloadProgress, shouldStop);

            Thread downloadThread = startDownloadingInSeparateThread(fileStream, gameFile, filePath);
            downloadThread.start();

            await().untilAsserted(() -> verify(downloadProgress).track(any()));
            downloadService.cancelDownload(filePath);
            downloadThread.join();
        } finally {
            shouldStop.set(true);
        }
    }

    @Test
    void cancelDownloadShouldNotThrowGivenFileIsNotCurrentlyBeingDownloaded() {
        assertThatCode(() -> downloadService.cancelDownload("nonExistentFilePath"))
                .doesNotThrowAnyException();
    }
}