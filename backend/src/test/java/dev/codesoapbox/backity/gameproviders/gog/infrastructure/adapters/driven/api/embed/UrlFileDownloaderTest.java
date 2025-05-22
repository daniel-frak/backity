package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.embed;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgress;
import dev.codesoapbox.backity.core.storagesolution.domain.FakeUnixStorageSolution;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.embed.exceptions.FileDownloadException;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.embed.testing.FakeProgressAwareFileStreamFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Clock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlFileDownloaderTest {

    private UrlFileDownloader urlFileDownloader;
    private FakeUnixStorageSolution storageSolution;
    private FakeProgressAwareFileStreamFactory fileStreamFactory;

    @Mock
    private Clock clock;

    @BeforeEach
    void setUp() {
        storageSolution = new FakeUnixStorageSolution();
        fileStreamFactory = new FakeProgressAwareFileStreamFactory(clock);
        urlFileDownloader = new UrlFileDownloader();
    }

    @Test
    void downloadFileShouldDownloadToDisk() throws IOException {
        GameFile gameFile = TestGameFile.gog();
        String filePath = "testFilePath";
        DownloadProgress downloadProgress = mockDownloadProgress();
        TrackableFileStream fileStream = fileStreamFactory.create(downloadProgress, "Test data");

        urlFileDownloader.downloadFile(storageSolution, fileStream, gameFile, filePath);

        assertThat(storageSolution.fileExists(filePath)).isTrue();
    }

    private DownloadProgress mockDownloadProgress() {
        DownloadProgress downloadProgress = mock(DownloadProgress.class);
        when(downloadProgress.track(any()))
                .thenAnswer(inv -> inv.getArgument(0));
        when(downloadProgress.getContentLengthBytes())
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

        urlFileDownloader.downloadFile(storageSolution, fileStream, gameFile, "someFilePath");

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

        assertThatThrownBy(() -> urlFileDownloader.downloadFile(storageSolution, fileStream, gameFile, filePath))
                .isInstanceOf(FileDownloadException.class)
                .message()
                .isEqualTo(
                        "The downloaded size of someFilePath is not what was expected (was 999, expected 9)");
    }
}