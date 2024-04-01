package dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services;

import dev.codesoapbox.backity.core.discovery.domain.ProgressInfo;
import dev.codesoapbox.backity.core.filemanagement.domain.FakeUnixFileManager;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetails;
import dev.codesoapbox.backity.core.gamefiledetails.domain.TestGameFileDetails;
import dev.codesoapbox.backity.integrations.gog.domain.exceptions.FileBackupException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
        urlFileDownloader = new UrlFileDownloader(fileManager, progressInfoConsumer);
    }

    @Test
    void downloadGameFileShouldDownloadToDisk() throws IOException {
        String originalFileName = "originalFileName.txt";
        GameFileDetails gameFileDetails = TestGameFileDetails.discoveredFileDetails()
                .originalFileName(originalFileName)
                .build();
        fileBufferProvider.mockDataForDownload(gameFileDetails, "Test data");

        urlFileDownloader.downloadGameFile(fileBufferProvider, gameFileDetails, "tempFilePath");

        fileManager.containsFile(originalFileName);
    }

    @Test
    void downloadGameFileShouldReturnDownloadedFilePath() throws IOException {
        String originalFileName = "originalFileName.txt";
        String tempFilePath = "tempFilePath";
        GameFileDetails gameFileDetails = TestGameFileDetails.discoveredFileDetails()
                .originalFileName(originalFileName)
                .build();
        fileBufferProvider.mockDataForDownload(gameFileDetails, "Test data");

        String filePath = urlFileDownloader.downloadGameFile(fileBufferProvider, gameFileDetails, tempFilePath);

        assertThat(filePath).isEqualTo(originalFileName);
    }

    @Test
    void downloadGameFileShouldUpdateProgress() throws IOException {
        GameFileDetails gameFileDetails = TestGameFileDetails.discoveredFileDetails().build();
        fileBufferProvider.mockDataForDownload(gameFileDetails, "Test data");

        urlFileDownloader.downloadGameFile(fileBufferProvider, gameFileDetails, "tempFilePath");

        verify(progressInfoConsumer, times(1)).accept(progressInfoArgumentCaptor.capture());
        assertThat(progressInfoArgumentCaptor.getAllValues())
                .extracting(ProgressInfo::percentage)
                .containsExactly(100);
    }

    @Test
    void downloadGameFileShouldDownloadAndUpdateProgressAndRename() throws IOException {
        String originalFileName = "originalFileName.txt";
        String tempFilePath = "tempFilePath";
        GameFileDetails gameFileDetails = TestGameFileDetails.discoveredFileDetails()
                .originalFileName(originalFileName)
                .build();
        fileBufferProvider.mockDataForDownload(gameFileDetails, "Test data");

        urlFileDownloader.downloadGameFile(fileBufferProvider, gameFileDetails, tempFilePath);

        assertThat(fileManager.fileWasRenamed(tempFilePath, originalFileName)).isTrue();
    }

    @Test
    void downloadGameFileShouldThrowGivenFileSizeDoesNotMatch() {
        String tempFilePath = "tempFilePath";
        GameFileDetails gameFileDetails = TestGameFileDetails.discoveredFileDetails().build();
        fileBufferProvider.mockDataForDownload(gameFileDetails, "Test data");
        fileManager.overrideDownloadedSizeFor(tempFilePath, 999L);

        assertThatThrownBy(() -> urlFileDownloader.downloadGameFile(fileBufferProvider, gameFileDetails, tempFilePath))
                .isInstanceOf(FileBackupException.class)
                .message()
                .isEqualTo(
                        "The downloaded size of tempFilePath is not what was expected (was 999, expected 9)");
    }
}