package dev.codesoapbox.backity.core.backup.domain;

import dev.codesoapbox.backity.core.backup.domain.exceptions.FileBackupFailedException;
import dev.codesoapbox.backity.core.backup.domain.exceptions.FileBackupUrlEmptyException;
import dev.codesoapbox.backity.core.backup.domain.exceptions.NotEnoughFreeSpaceException;
import dev.codesoapbox.backity.core.filemanagement.domain.FakeUnixFileManager;
import dev.codesoapbox.backity.core.filemanagement.domain.FilePathProvider;
import dev.codesoapbox.backity.core.gamefiledetails.domain.FileBackupStatus;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetails;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetailsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dev.codesoapbox.backity.core.gamefiledetails.domain.TestGameFileDetails.discoveredFileDetails;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileBackupServiceTest {

    private FileBackupService fileBackupService;

    @Mock
    private FilePathProvider filePathProvider;

    @Mock
    private GameFileDetailsRepository gameFileDetailsRepository;

    @Mock
    private SourceFileBackupService sourceFileBackupService;

    private FakeUnixFileManager fileManager;

    @BeforeEach
    void setUp() {
        when(sourceFileBackupService.getSource())
                .thenReturn(new FileSourceId("someSourceId"));
        fileManager = new FakeUnixFileManager(5000);
        fileBackupService = new FileBackupService(filePathProvider, gameFileDetailsRepository, fileManager,
                singletonList(sourceFileBackupService));
    }

    @Test
    void shouldDownloadGameFile() throws IOException {
        FileSourceId source = sourceFileBackupService.getSource();
        GameFileDetails gameFileDetails = discoveredFileDetails().build();
        var tempFilePath = "someFileDir/someFile";
        var expectedFilePath = "finalFilePath";
        List<FileBackupStatus> savedFileBackupStatuses = new ArrayList<>();
        List<String> savedFilePaths = new ArrayList<>();
        when(gameFileDetailsRepository.save(any()))
                .then(a -> {
                    GameFileDetails argument = a.getArgument(0, GameFileDetails.class);
                    savedFileBackupStatuses.add(argument.getBackupDetails().getStatus());
                    savedFilePaths.add(argument.getBackupDetails().getFilePath());
                    return argument;
                });
        when(filePathProvider.createTemporaryFilePath(source,
                gameFileDetails.getSourceFileDetails().originalGameTitle()))
                .thenReturn(tempFilePath);
        when(sourceFileBackupService.backUpGameFile(gameFileDetails, tempFilePath))
                .thenReturn(expectedFilePath);

        fileBackupService.backUpGameFile(gameFileDetails);

        assertThat(fileManager.freeSpaceWasCheckedFor(tempFilePath)).isTrue();
        assertThat(savedFileBackupStatuses)
                .isEqualTo(List.of(FileBackupStatus.IN_PROGRESS, FileBackupStatus.IN_PROGRESS,
                        FileBackupStatus.SUCCESS));
        assertThat(savedFilePaths)
                .isEqualTo(Arrays.asList(null, tempFilePath, expectedFilePath));
    }

    @Test
    void shouldTryToRemoveTempFileAndRethrowWrappedOnIOExceptionGivenFilePathIsTempFilePath() throws IOException {
        FileSourceId source = sourceFileBackupService.getSource();
        GameFileDetails gameFileDetails = discoveredFileDetails().build();
        var tempFilePath = "someFileDir/someFile";
        var coreException = new IOException("someMessage");
        when(filePathProvider.createTemporaryFilePath(source,
                gameFileDetails.getSourceFileDetails().originalGameTitle()))
                .thenReturn(tempFilePath);
        when(sourceFileBackupService.backUpGameFile(gameFileDetails, tempFilePath))
                .thenThrow(coreException);

        assertThatThrownBy(() -> fileBackupService.backUpGameFile(gameFileDetails))
                .isInstanceOf(FileBackupFailedException.class)
                .cause().isEqualTo(coreException);
        assertThat(gameFileDetails.getBackupDetails())
                .satisfies(backupDetails -> assertSoftly(softly -> {
                    softly.assertThat(backupDetails.getStatus()).isEqualTo(FileBackupStatus.FAILED);
                    softly.assertThat(backupDetails.getFailedReason()).isEqualTo(coreException.getMessage());
                }));
        verify(gameFileDetailsRepository, times(4)).save(gameFileDetails);
        assertThat(fileManager.fileDeleteWasAttempted(tempFilePath)).isTrue();
        assertThat(gameFileDetails.getBackupDetails().getFilePath()).isNull();
    }

    @Test
    void shouldTryToRemoveTempFileAndRethrowWrappedOnIOExceptionGivenFilePathIsNotTempFilePath() throws IOException {
        FileSourceId source = sourceFileBackupService.getSource();
        GameFileDetails gameFileDetails = discoveredFileDetails().build();
        var tempFilePath = "someFileDir/someFile";
        var nonTempFilePath = "nonTempFilePath";
        var coreException = new IOException("someMessage");
        when(filePathProvider.createTemporaryFilePath(source,
                gameFileDetails.getSourceFileDetails().originalGameTitle()))
                .thenReturn(tempFilePath);
        when(sourceFileBackupService.backUpGameFile(gameFileDetails, tempFilePath))
                .thenAnswer(inv -> {
                    gameFileDetails.getBackupDetails().setFilePath(nonTempFilePath);
                    throw coreException;
                });

        assertThatThrownBy(() -> fileBackupService.backUpGameFile(gameFileDetails))
                .isInstanceOf(FileBackupFailedException.class)
                .cause().isEqualTo(coreException);
        assertThat(gameFileDetails.getBackupDetails())
                .satisfies(backupDetails -> assertSoftly(softly -> {
                    softly.assertThat(backupDetails.getStatus()).isEqualTo(FileBackupStatus.FAILED);
                    softly.assertThat(backupDetails.getFailedReason()).isEqualTo(coreException.getMessage());
                }));
        verify(gameFileDetailsRepository, times(3)).save(gameFileDetails);
        assertThat(gameFileDetails.getBackupDetails().getFilePath()).isEqualTo(nonTempFilePath);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void downloadGameFileShouldThrowIfUrlIsEmpty(String url) {
        GameFileDetails gameFileDetails = discoveredFileDetails()
                .url(url)
                .build();

        assertThatThrownBy(() -> fileBackupService.backUpGameFile(gameFileDetails))
                .isInstanceOf(FileBackupFailedException.class)
                .cause().isInstanceOf(FileBackupUrlEmptyException.class);
    }

    @Test
    void downloadGameFileShouldThrowIfSourceDownloaderNotFound() throws IOException {
        var sourceId = new FileSourceId("nonExistentSource1");
        GameFileDetails gameFileDetails = discoveredFileDetails()
                .sourceId(sourceId)
                .build();
        var tempFilePath = "someFileDir/someFile";
        when(filePathProvider.createTemporaryFilePath(sourceId,
                gameFileDetails.getSourceFileDetails().originalGameTitle()))
                .thenReturn(tempFilePath);

        assertThatThrownBy(() -> fileBackupService.backUpGameFile(gameFileDetails))
                .isInstanceOf(FileBackupFailedException.class)
                .cause().isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void downloadGameFileShouldThrowIfIOExceptionOccurs() throws IOException {
        FileSourceId sourceId = sourceFileBackupService.getSource();
        GameFileDetails gameFileDetails = discoveredFileDetails()
                .sourceId(sourceId)
                .build();

        when(filePathProvider.createTemporaryFilePath(sourceId,
                gameFileDetails.getSourceFileDetails().originalGameTitle()))
                .thenThrow(new IOException());

        assertThatThrownBy(() -> fileBackupService.backUpGameFile(gameFileDetails))
                .isInstanceOf(FileBackupFailedException.class)
                .cause().isInstanceOf(IOException.class);
    }

    @Test
    void downloadGameFileShouldThrowIfNotEnoughFreeSpace() throws IOException {
        FileSourceId sourceId = sourceFileBackupService.getSource();
        GameFileDetails gameFileDetails = discoveredFileDetails()
                .sourceId(sourceId)
                .build();
        var tempFilePath = "someFileDir/someFile";
        fileManager.setAvailableSizeInBytes(0);
        when(filePathProvider.createTemporaryFilePath(sourceId,
                gameFileDetails.getSourceFileDetails().originalGameTitle()))
                .thenReturn(tempFilePath);

        assertThatThrownBy(() -> fileBackupService.backUpGameFile(gameFileDetails))
                .isInstanceOf(FileBackupFailedException.class)
                .cause().isInstanceOf(NotEnoughFreeSpaceException.class);
    }

    @Test
    void isReadyForShouldReturnTrueIfFileIsReadyToDownload() {
        GameFileDetails gameFileDetails = discoveredFileDetails().build();
        when(sourceFileBackupService.isReady())
                .thenReturn(true);

        assertThat(fileBackupService.isReadyFor(gameFileDetails)).isTrue();
    }

    @Test
    void isReadyForShouldReturnFalseIfFileIsNotReadyToDownload() {
        GameFileDetails gameFileDetails = discoveredFileDetails().build();
        when(sourceFileBackupService.isReady())
                .thenReturn(false);

        assertThat(fileBackupService.isReadyFor(gameFileDetails)).isFalse();
    }
}