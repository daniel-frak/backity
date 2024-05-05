package dev.codesoapbox.backity.core.backup.domain;

import dev.codesoapbox.backity.core.backup.domain.exceptions.FileBackupFailedException;
import dev.codesoapbox.backity.core.backup.domain.exceptions.FileBackupUrlEmptyException;
import dev.codesoapbox.backity.core.backup.domain.exceptions.NotEnoughFreeSpaceException;
import dev.codesoapbox.backity.core.filedetails.domain.FileBackupStatus;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetailsRepository;
import dev.codesoapbox.backity.core.filemanagement.domain.FakeUnixFileManager;
import dev.codesoapbox.backity.core.filemanagement.domain.FilePathProvider;
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

import static dev.codesoapbox.backity.core.filedetails.domain.TestFileDetails.discoveredFileDetails;
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
    private FileDetailsRepository fileDetailsRepository;

    @Mock
    private SourceFileBackupService sourceFileBackupService;

    private FakeUnixFileManager fileManager;

    @BeforeEach
    void setUp() {
        when(sourceFileBackupService.getSource())
                .thenReturn(new FileSourceId("someSourceId"));
        fileManager = new FakeUnixFileManager(5000);
        fileBackupService = new FileBackupService(filePathProvider, fileDetailsRepository, fileManager,
                singletonList(sourceFileBackupService));
    }

    @Test
    void shouldDownloadFile() throws IOException {
        FileSourceId source = sourceFileBackupService.getSource();
        FileDetails fileDetails = discoveredFileDetails().build();
        var tempFilePath = "someFileDir/someFile";
        var expectedFilePath = "finalFilePath";
        List<FileBackupStatus> savedFileBackupStatuses = new ArrayList<>();
        List<String> savedFilePaths = new ArrayList<>();
        when(fileDetailsRepository.save(any()))
                .then(a -> {
                    FileDetails argument = a.getArgument(0, FileDetails.class);
                    savedFileBackupStatuses.add(argument.getBackupDetails().getStatus());
                    savedFilePaths.add(argument.getBackupDetails().getFilePath());
                    return argument;
                });
        when(filePathProvider.createTemporaryFilePath(source,
                fileDetails.getSourceFileDetails().originalGameTitle()))
                .thenReturn(tempFilePath);
        when(sourceFileBackupService.backUpFile(fileDetails, tempFilePath))
                .thenReturn(expectedFilePath);

        fileBackupService.backUpFile(fileDetails);

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
        FileDetails fileDetails = discoveredFileDetails().build();
        var tempFilePath = "someFileDir/someFile";
        var coreException = new IOException("someMessage");
        when(filePathProvider.createTemporaryFilePath(source,
                fileDetails.getSourceFileDetails().originalGameTitle()))
                .thenReturn(tempFilePath);
        when(sourceFileBackupService.backUpFile(fileDetails, tempFilePath))
                .thenThrow(coreException);

        assertThatThrownBy(() -> fileBackupService.backUpFile(fileDetails))
                .isInstanceOf(FileBackupFailedException.class)
                .cause().isEqualTo(coreException);
        assertThat(fileDetails.getBackupDetails())
                .satisfies(backupDetails -> assertSoftly(softly -> {
                    softly.assertThat(backupDetails.getStatus()).isEqualTo(FileBackupStatus.FAILED);
                    softly.assertThat(backupDetails.getFailedReason()).isEqualTo(coreException.getMessage());
                }));
        verify(fileDetailsRepository, times(4)).save(fileDetails);
        assertThat(fileManager.fileDeleteWasAttempted(tempFilePath)).isTrue();
        assertThat(fileDetails.getBackupDetails().getFilePath()).isNull();
    }

    @Test
    void shouldTryToRemoveTempFileAndRethrowWrappedOnIOExceptionGivenFilePathIsNotTempFilePath() throws IOException {
        FileSourceId source = sourceFileBackupService.getSource();
        FileDetails fileDetails = discoveredFileDetails().build();
        var tempFilePath = "someFileDir/someFile";
        var nonTempFilePath = "nonTempFilePath";
        var coreException = new IOException("someMessage");
        when(filePathProvider.createTemporaryFilePath(source,
                fileDetails.getSourceFileDetails().originalGameTitle()))
                .thenReturn(tempFilePath);
        when(sourceFileBackupService.backUpFile(fileDetails, tempFilePath))
                .thenAnswer(inv -> {
                    fileDetails.getBackupDetails().setFilePath(nonTempFilePath);
                    throw coreException;
                });

        assertThatThrownBy(() -> fileBackupService.backUpFile(fileDetails))
                .isInstanceOf(FileBackupFailedException.class)
                .cause().isEqualTo(coreException);
        assertThat(fileDetails.getBackupDetails())
                .satisfies(backupDetails -> assertSoftly(softly -> {
                    softly.assertThat(backupDetails.getStatus()).isEqualTo(FileBackupStatus.FAILED);
                    softly.assertThat(backupDetails.getFailedReason()).isEqualTo(coreException.getMessage());
                }));
        verify(fileDetailsRepository, times(3)).save(fileDetails);
        assertThat(fileDetails.getBackupDetails().getFilePath()).isEqualTo(nonTempFilePath);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void downloadFileShouldThrowIfUrlIsEmpty(String url) {
        FileDetails fileDetails = discoveredFileDetails()
                .url(url)
                .build();

        assertThatThrownBy(() -> fileBackupService.backUpFile(fileDetails))
                .isInstanceOf(FileBackupFailedException.class)
                .cause().isInstanceOf(FileBackupUrlEmptyException.class);
    }

    @Test
    void downloadFileShouldThrowIfSourceDownloaderNotFound() throws IOException {
        var sourceId = new FileSourceId("nonExistentSource1");
        FileDetails fileDetails = discoveredFileDetails()
                .sourceId(sourceId)
                .build();
        var tempFilePath = "someFileDir/someFile";
        when(filePathProvider.createTemporaryFilePath(sourceId,
                fileDetails.getSourceFileDetails().originalGameTitle()))
                .thenReturn(tempFilePath);

        assertThatThrownBy(() -> fileBackupService.backUpFile(fileDetails))
                .isInstanceOf(FileBackupFailedException.class)
                .cause().isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void downloadFileShouldThrowIfIOExceptionOccurs() throws IOException {
        FileSourceId sourceId = sourceFileBackupService.getSource();
        FileDetails fileDetails = discoveredFileDetails()
                .sourceId(sourceId)
                .build();

        when(filePathProvider.createTemporaryFilePath(sourceId,
                fileDetails.getSourceFileDetails().originalGameTitle()))
                .thenThrow(new IOException());

        assertThatThrownBy(() -> fileBackupService.backUpFile(fileDetails))
                .isInstanceOf(FileBackupFailedException.class)
                .cause().isInstanceOf(IOException.class);
    }

    @Test
    void downloadFileShouldThrowIfNotEnoughFreeSpace() throws IOException {
        FileSourceId sourceId = sourceFileBackupService.getSource();
        FileDetails fileDetails = discoveredFileDetails()
                .sourceId(sourceId)
                .build();
        var tempFilePath = "someFileDir/someFile";
        fileManager.setAvailableSizeInBytes(0);
        when(filePathProvider.createTemporaryFilePath(sourceId,
                fileDetails.getSourceFileDetails().originalGameTitle()))
                .thenReturn(tempFilePath);

        assertThatThrownBy(() -> fileBackupService.backUpFile(fileDetails))
                .isInstanceOf(FileBackupFailedException.class)
                .cause().isInstanceOf(NotEnoughFreeSpaceException.class);
    }

    @Test
    void isReadyForShouldReturnTrueIfFileIsReadyToDownload() {
        FileDetails fileDetails = discoveredFileDetails().build();
        when(sourceFileBackupService.isReady())
                .thenReturn(true);

        assertThat(fileBackupService.isReadyFor(fileDetails)).isTrue();
    }

    @Test
    void isReadyForShouldReturnFalseIfFileIsNotReadyToDownload() {
        FileDetails fileDetails = discoveredFileDetails().build();
        when(sourceFileBackupService.isReady())
                .thenReturn(false);

        assertThat(fileBackupService.isReadyFor(fileDetails)).isFalse();
    }
}