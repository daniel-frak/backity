package dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services;

import dev.codesoapbox.backity.core.backup.domain.BackupProgress;
import dev.codesoapbox.backity.core.backup.domain.BackupProgressFactory;
import dev.codesoapbox.backity.core.filemanagement.domain.FakeUnixFileManager;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
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

import static dev.codesoapbox.backity.core.gamefile.domain.TestGameFile.discoveredGameFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlFileDownloaderTest {

    private UrlFileDownloader urlFileDownloader;
    private FakeUnixFileManager fileManager;

    private FakeFileBufferProvider fileBufferProvider;

    @Mock
    private BackupProgressFactory backupProgressFactory;

    @Mock
    private Clock clock;

    @BeforeEach
    void setUp() {
        fileManager = new FakeUnixFileManager();
        fileBufferProvider = new FakeFileBufferProvider(clock);
        urlFileDownloader = new UrlFileDownloader(fileManager, backupProgressFactory);
    }

    @Test
    void downloadFileShouldDownloadToDisk() throws IOException {
        String originalFileName = "originalFileName.txt";
        GameFile gameFile = discoveredGameFile()
                .originalFileName(originalFileName)
                .build();
        fileBufferProvider.mockDataForDownload(gameFile, "Test data");
        mockBackupProgressCreation();

        urlFileDownloader.downloadFile(fileBufferProvider, gameFile, "tempFilePath");

        assertThat(fileManager.containsFile(originalFileName)).isTrue();
    }

    private BackupProgress mockBackupProgressCreation() {
        BackupProgress backupProgress = mock(BackupProgress.class);
        when(backupProgress.track(any()))
                .thenAnswer(inv -> inv.getArgument(0));
        when(backupProgressFactory.create())
                .thenReturn(backupProgress);
        when(backupProgress.getContentLengthBytes())
                .thenReturn(9L);

        return backupProgress;
    }

    @Test
    void downloadFileShouldTrackProgress() throws IOException {
        BackupProgress backupProgress = mockBackupProgressCreation();
        fileManager = new FakeUnixFileManager();
        fileBufferProvider = new FakeFileBufferProvider(clock);
        urlFileDownloader = new UrlFileDownloader(fileManager, backupProgressFactory);
        when(backupProgress.track(any()))
                .thenAnswer(inv -> inv.getArgument(0));
        when(backupProgress.getContentLengthBytes())
                .thenReturn(9L);
        GameFile gameFile = discoveredGameFile().build();
        fileBufferProvider.mockDataForDownload(gameFile, "Test data");

        urlFileDownloader.downloadFile(fileBufferProvider, gameFile, "tempFilePath");

        InOrder inOrder = Mockito.inOrder(backupProgress);
        inOrder.verify(backupProgress).initializeTracking(9, clock);
        inOrder.verify(backupProgress).track(any());
    }

    @Test
    void downloadFileShouldReturnDownloadedFilePath() throws IOException {
        String originalFileName = "originalFileName.txt";
        String tempFilePath = "tempFilePath";
        GameFile gameFile = discoveredGameFile()
                .originalFileName(originalFileName)
                .build();
        fileBufferProvider.mockDataForDownload(gameFile, "Test data");
        mockBackupProgressCreation();

        String filePath = urlFileDownloader.downloadFile(fileBufferProvider, gameFile, tempFilePath);

        assertThat(filePath).isEqualTo(originalFileName);
    }

    @Test
    void downloadFileShouldDownloadAndUpdateProgressAndRename() throws IOException {
        String originalFileName = "originalFileName.txt";
        String tempFilePath = "tempFilePath";
        GameFile gameFile = discoveredGameFile()
                .originalFileName(originalFileName)
                .build();
        fileBufferProvider.mockDataForDownload(gameFile, "Test data");
        mockBackupProgressCreation();

        urlFileDownloader.downloadFile(fileBufferProvider, gameFile, tempFilePath);

        assertThat(fileManager.fileWasRenamed(tempFilePath, originalFileName)).isTrue();
    }

    @Test
    void downloadFileShouldThrowGivenFileSizeDoesNotMatch() {
        String tempFilePath = "tempFilePath";
        GameFile gameFile = discoveredGameFile().build();
        fileBufferProvider.mockDataForDownload(gameFile, "Test data");
        fileManager.overrideDownloadedSizeFor(tempFilePath, 999L);
        mockBackupProgressCreation();

        assertThatThrownBy(() -> urlFileDownloader.downloadFile(fileBufferProvider, gameFile, tempFilePath))
                .isInstanceOf(FileBackupException.class)
                .message()
                .isEqualTo(
                        "The downloaded size of tempFilePath is not what was expected (was 999, expected 9)");
    }
}