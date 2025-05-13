package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.backups;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.BackupProgress;
import dev.codesoapbox.backity.core.filemanagement.domain.FakeUnixFileSystem;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.gameproviders.gog.application.TrackableFileStream;
import dev.codesoapbox.backity.gameproviders.gog.domain.exceptions.FileBackupException;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.backups.testing.FakeProgressAwareFileStreamFactory;
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
    private FakeUnixFileSystem fileManager;
    private FakeProgressAwareFileStreamFactory fileStreamFactory;

    @Mock
    private Clock clock;

    @BeforeEach
    void setUp() {
        fileManager = new FakeUnixFileSystem();
        fileStreamFactory = new FakeProgressAwareFileStreamFactory(clock);
        urlFileDownloader = new UrlFileDownloader(fileManager);
    }

    @Test
    void downloadFileShouldDownloadToDisk() throws IOException {
        GameFile gameFile = TestGameFile.discovered();
        String filePath = "testFilePath";
        BackupProgress backupProgress = mockBackupProgress();
        TrackableFileStream fileStream = fileStreamFactory.create(backupProgress, "Test data");

        urlFileDownloader.downloadFile(fileStream, gameFile, filePath);

        assertThat(fileManager.fileExists(filePath)).isTrue();
    }

    private BackupProgress mockBackupProgress() {
        BackupProgress backupProgress = mock(BackupProgress.class);
        when(backupProgress.track(any()))
                .thenAnswer(inv -> inv.getArgument(0));
        when(backupProgress.getContentLengthBytes())
                .thenReturn(9L);

        return backupProgress;
    }

    @Test
    void downloadFileShouldTrackProgress() throws IOException {
        BackupProgress backupProgress = mockBackupProgress();
        fileManager = new FakeUnixFileSystem();
        fileStreamFactory = new FakeProgressAwareFileStreamFactory(clock);
        urlFileDownloader = new UrlFileDownloader(fileManager);
        when(backupProgress.track(any()))
                .thenAnswer(inv -> inv.getArgument(0));
        when(backupProgress.getContentLengthBytes())
                .thenReturn(9L);
        GameFile gameFile = TestGameFile.discovered();
        TrackableFileStream fileStream = fileStreamFactory.create(backupProgress, "Test data");

        urlFileDownloader.downloadFile(fileStream, gameFile, "someFilePath");

        InOrder inOrder = Mockito.inOrder(backupProgress);
        inOrder.verify(backupProgress).initializeTracking(9, clock);
        inOrder.verify(backupProgress).track(any());
    }

    @Test
    void downloadFileShouldThrowGivenFileSizeDoesNotMatch() {
        String filePath = "someFilePath";
        GameFile gameFile = TestGameFile.discovered();
        fileManager.overrideDownloadedSizeFor(filePath, 999L);
        BackupProgress backupProgress = mockBackupProgress();
        TrackableFileStream fileStream = fileStreamFactory.create(backupProgress, "Test data");

        assertThatThrownBy(() -> urlFileDownloader.downloadFile(fileStream, gameFile, filePath))
                .isInstanceOf(FileBackupException.class)
                .message()
                .isEqualTo(
                        "The downloaded size of someFilePath is not what was expected (was 999, expected 9)");
    }
}