package dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services;

import dev.codesoapbox.backity.core.backup.domain.BackupProgress;
import dev.codesoapbox.backity.core.discovery.domain.ProgressInfo;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import dev.codesoapbox.backity.core.filemanagement.domain.FakeUnixFileManager;
import dev.codesoapbox.backity.integrations.gog.domain.exceptions.FileBackupException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static dev.codesoapbox.backity.core.filedetails.domain.TestFileDetails.discoveredFileDetails;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlFileDownloaderTest {

    private UrlFileDownloader urlFileDownloader;
    private FakeUnixFileManager fileManager;

    private FakeFileBufferProvider fileBufferProvider;

    @Mock
    private Consumer<ProgressInfo> progressInfoConsumer;

    @Captor
    private ArgumentCaptor<ProgressInfo> progressInfoArgumentCaptor;

    @BeforeEach
    void setUp() {
        fileManager = new FakeUnixFileManager();
        fileBufferProvider = new FakeFileBufferProvider();
        urlFileDownloader = new UrlFileDownloader(fileManager, progressInfoConsumer, BackupProgress::new);
    }

    @Test
    void downloadFileShouldDownloadToDisk() throws IOException {
        String originalFileName = "originalFileName.txt";
        FileDetails fileDetails = discoveredFileDetails()
                .originalFileName(originalFileName)
                .build();
        fileBufferProvider.mockDataForDownload(fileDetails, "Test data");

        urlFileDownloader.downloadFile(fileBufferProvider, fileDetails, "tempFilePath");

        assertThat(fileManager.containsFile(originalFileName)).isTrue();
    }

    @Test
    void downloadFileShouldTrackProgress() throws IOException {
        BackupProgress backupProgress = mock(BackupProgress.class);
        Supplier<BackupProgress> backupProgressFactory = () -> backupProgress;
        fileManager = new FakeUnixFileManager();
        fileBufferProvider = new FakeFileBufferProvider();
        urlFileDownloader = new UrlFileDownloader(fileManager, progressInfoConsumer, backupProgressFactory);
        when(backupProgress.getTrackedOutputStream(any()))
                .thenAnswer(inv -> inv.getArgument(0));
        when(backupProgress.getContentLengthBytes())
                .thenReturn(9L);
        FileDetails fileDetails = discoveredFileDetails().build();
        fileBufferProvider.mockDataForDownload(fileDetails, "Test data");

        urlFileDownloader.downloadFile(fileBufferProvider, fileDetails, "tempFilePath");

        InOrder inOrder = Mockito.inOrder(backupProgress);
        inOrder.verify(backupProgress).startTracking(9);
        inOrder.verify(backupProgress).subscribeToProgress(progressInfoConsumer);
        inOrder.verify(backupProgress).getTrackedOutputStream(any());
        inOrder.verify(backupProgress).unsubscribeFromProgress(progressInfoConsumer);
    }

    @Test
    void downloadFileShouldReturnDownloadedFilePath() throws IOException {
        String originalFileName = "originalFileName.txt";
        String tempFilePath = "tempFilePath";
        FileDetails fileDetails = discoveredFileDetails()
                .originalFileName(originalFileName)
                .build();
        fileBufferProvider.mockDataForDownload(fileDetails, "Test data");

        String filePath = urlFileDownloader.downloadFile(fileBufferProvider, fileDetails, tempFilePath);

        assertThat(filePath).isEqualTo(originalFileName);
    }

    @Test
    void downloadFileShouldUpdateProgress() throws IOException {
        FileDetails fileDetails = discoveredFileDetails().build();
        fileBufferProvider.mockDataForDownload(fileDetails, "Test data");

        urlFileDownloader.downloadFile(fileBufferProvider, fileDetails, "tempFilePath");

        verify(progressInfoConsumer, times(1)).accept(progressInfoArgumentCaptor.capture());
        assertThat(progressInfoArgumentCaptor.getAllValues())
                .extracting(ProgressInfo::percentage)
                .containsExactly(100);
    }

    @Test
    void downloadFileShouldDownloadAndUpdateProgressAndRename() throws IOException {
        String originalFileName = "originalFileName.txt";
        String tempFilePath = "tempFilePath";
        FileDetails fileDetails = discoveredFileDetails()
                .originalFileName(originalFileName)
                .build();
        fileBufferProvider.mockDataForDownload(fileDetails, "Test data");

        urlFileDownloader.downloadFile(fileBufferProvider, fileDetails, tempFilePath);

        assertThat(fileManager.fileWasRenamed(tempFilePath, originalFileName)).isTrue();
    }

    @Test
    void downloadFileShouldThrowGivenFileSizeDoesNotMatch() {
        String tempFilePath = "tempFilePath";
        FileDetails fileDetails = discoveredFileDetails().build();
        fileBufferProvider.mockDataForDownload(fileDetails, "Test data");
        fileManager.overrideDownloadedSizeFor(tempFilePath, 999L);

        assertThatThrownBy(() -> urlFileDownloader.downloadFile(fileBufferProvider, fileDetails, tempFilePath))
                .isInstanceOf(FileBackupException.class)
                .message()
                .isEqualTo(
                        "The downloaded size of tempFilePath is not what was expected (was 999, expected 9)");
    }
}