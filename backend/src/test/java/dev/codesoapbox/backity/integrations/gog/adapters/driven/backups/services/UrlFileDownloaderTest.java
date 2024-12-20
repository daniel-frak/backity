package dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services;

import dev.codesoapbox.backity.core.backup.domain.BackupProgress;
import dev.codesoapbox.backity.core.filemanagement.domain.FakeUnixFileManager;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.integrations.gog.domain.exceptions.FileBackupException;
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
    private FakeUnixFileManager fileManager;
    private FakeFileBufferProvider fileBufferProvider;

    @Mock
    private Clock clock;

    @BeforeEach
    void setUp() {
        fileManager = new FakeUnixFileManager();
        fileBufferProvider = new FakeFileBufferProvider(clock);
        urlFileDownloader = new UrlFileDownloader(fileManager);
    }

    @Test
    void downloadFileShouldDownloadToDisk() throws IOException {
        GameFile gameFile = TestGameFile.discovered();
        String originalFileName = gameFile.getGameProviderFile().originalFileName();
        fileBufferProvider.mockDataForDownload(gameFile, "Test data");
        BackupProgress backupProgress = mockBackupProgress();

        urlFileDownloader.downloadFile(fileBufferProvider, gameFile, "tempFilePath", backupProgress);

        assertThat(fileManager.containsFile(originalFileName)).isTrue();
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
        fileManager = new FakeUnixFileManager();
        fileBufferProvider = new FakeFileBufferProvider(clock);
        urlFileDownloader = new UrlFileDownloader(fileManager);
        when(backupProgress.track(any()))
                .thenAnswer(inv -> inv.getArgument(0));
        when(backupProgress.getContentLengthBytes())
                .thenReturn(9L);
        GameFile gameFile = TestGameFile.discovered();
        fileBufferProvider.mockDataForDownload(gameFile, "Test data");

        urlFileDownloader.downloadFile(fileBufferProvider, gameFile, "tempFilePath", backupProgress);

        InOrder inOrder = Mockito.inOrder(backupProgress);
        inOrder.verify(backupProgress).initializeTracking(9, clock);
        inOrder.verify(backupProgress).track(any());
    }

    @Test
    void downloadFileShouldReturnDownloadedFilePath() throws IOException {
        String originalFileName = "originalFileName.txt";
        String tempFilePath = "tempFilePath";
        GameFile gameFile = TestGameFile.discoveredBuilder()
                .originalFileName(originalFileName)
                .build();
        fileBufferProvider.mockDataForDownload(gameFile, "Test data");
        BackupProgress backupProgress = mockBackupProgress();

        String filePath = urlFileDownloader.downloadFile(fileBufferProvider, gameFile, tempFilePath, backupProgress);

        assertThat(filePath).isEqualTo(originalFileName);
    }

    @Test
    void downloadFileShouldDownloadAndUpdateProgressAndRename() throws IOException {
        String originalFileName = "originalFileName.txt";
        String tempFilePath = "tempFilePath";
        GameFile gameFile = TestGameFile.discoveredBuilder()
                .originalFileName(originalFileName)
                .build();
        fileBufferProvider.mockDataForDownload(gameFile, "Test data");
        BackupProgress backupProgress = mockBackupProgress();

        urlFileDownloader.downloadFile(fileBufferProvider, gameFile, tempFilePath, backupProgress);

        assertThat(fileManager.fileWasRenamed(tempFilePath, originalFileName)).isTrue();
    }

    @Test
    void downloadFileShouldThrowGivenFileSizeDoesNotMatch() {
        String tempFilePath = "tempFilePath";
        GameFile gameFile = TestGameFile.discovered();
        fileBufferProvider.mockDataForDownload(gameFile, "Test data");
        fileManager.overrideDownloadedSizeFor(tempFilePath, 999L);
        BackupProgress backupProgress = mockBackupProgress();

        assertThatThrownBy(() -> urlFileDownloader.downloadFile(
                fileBufferProvider, gameFile, tempFilePath, backupProgress))
                .isInstanceOf(FileBackupException.class)
                .message()
                .isEqualTo(
                        "The downloaded size of tempFilePath is not what was expected (was 999, expected 9)");
    }
}