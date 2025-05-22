package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgressFactory;
import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.backup.domain.exceptions.FileBackupFailedException;
import dev.codesoapbox.backity.core.filecopy.domain.FileBackupStatus;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.gamefile.domain.*;
import dev.codesoapbox.backity.core.storagesolution.domain.FakeUnixStorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.UniqueFilePathResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileBackupServiceTest {

    private static final GameProviderId EXISTING_GAME_PROVIDER_ID = new GameProviderId("GOG");

    private FileBackupService fileBackupService;

    @Mock
    private UniqueFilePathResolver uniqueFilePathResolver;

    @Mock
    private FileCopyRepository fileCopyRepository;

    @Mock
    private GameProviderFileBackupService gameProviderFileBackupService;

    @Mock
    private DownloadProgressFactory downloadProgressFactory;

    private FakeUnixStorageSolution storageSolution;

    @BeforeEach
    void setUp() {
        when(gameProviderFileBackupService.getGameProviderId())
                .thenReturn(EXISTING_GAME_PROVIDER_ID);
        storageSolution = new FakeUnixStorageSolution(5120);
        fileBackupService = new FileBackupService(uniqueFilePathResolver, fileCopyRepository, storageSolution,
                singletonList(gameProviderFileBackupService), downloadProgressFactory);
    }

    @Test
    void shouldDownloadFile() {
        GameFile gameFile = TestGameFile.gog();
        FileCopy fileCopy = TestFileCopy.discovered();
        PersistedChanges persistedChanges = trackPersistedChanges();
        String expectedFilePath = mockFilePathCreation(gameFile);

        fileBackupService.backUpFile(gameFile, fileCopy);

        assertThat(persistedChanges.savedFileBackupStatuses())
                .isEqualTo(List.of(FileBackupStatus.IN_PROGRESS, FileBackupStatus.IN_PROGRESS,
                        FileBackupStatus.SUCCESS));
        assertThat(persistedChanges.savedFilePaths())
                .isEqualTo(Arrays.asList(null, // Mark 'in progress' with no file path
                        expectedFilePath, // Set file path before starting download
                        expectedFilePath // Mark 'done'
                ));
    }

    private String mockFilePathCreation(GameFile gameFile) {
        var filePath = "someFileDir/someFile";
        when(uniqueFilePathResolver.resolve(gameFile.getFileSource()))
                .thenReturn(filePath);

        return filePath;
    }

    private PersistedChanges trackPersistedChanges() {
        var persistedChanges = new PersistedChanges();
        when(fileCopyRepository.save(any()))
                .then(a -> {
                    FileCopy fileCopy = a.getArgument(0, FileCopy.class);
                    persistedChanges.addFor(fileCopy);

                    return fileCopy;
                });

        return persistedChanges;
    }

    @Test
    void shouldTryToRemoveFileAndRethrowWrappedGivenIOException() throws IOException {
        GameFile gameFile = TestGameFile.gog();
        FileCopy fileCopy = TestFileCopy.discovered();
        String filePath = mockFilePathCreation(gameFile);
        IOException coreException = mockGameProviderServiceThrowsExceptionDuringBackup();

        assertThatThrownBy(() -> fileBackupService.backUpFile(gameFile, fileCopy))
                .isInstanceOf(FileBackupFailedException.class)
                .hasCause(coreException);
        assertThat(fileCopy)
                .satisfies(it -> assertSoftly(softly -> {
                    softly.assertThat(it.getStatus()).isEqualTo(FileBackupStatus.FAILED);
                    softly.assertThat(it.getFailedReason()).isEqualTo(coreException.getMessage());
                }));
        verify(fileCopyRepository, times(4)).save(fileCopy);
        assertThat(storageSolution.fileDeleteWasAttempted(filePath)).isTrue();
        assertThat(fileCopy.getFilePath()).isNull();
    }

    private IOException mockGameProviderServiceThrowsExceptionDuringBackup() throws IOException {
        var coreException = new IOException("someMessage");
        doThrow(coreException)
                .when(gameProviderFileBackupService).backUpFile(any(), any(), any());

        return coreException;
    }

    @Test
    void downloadFileShouldThrowIfGameProviderFileBackupServiceNotFound() {
        var nonExistentGameProviderId = new GameProviderId("nonExistentGameProviderId1");
        GameFile gameFile = TestGameFile.gogBuilder()
                .fileSource(TestFileSource.minimalGogBuilder()
                        .gameProviderId(nonExistentGameProviderId)
                        .build())
                .build();
        FileCopy fileCopy = TestFileCopy.discovered();
        mockFilePathCreation(gameFile);

        assertThatThrownBy(() -> fileBackupService.backUpFile(gameFile, fileCopy))
                .isInstanceOf(FileBackupFailedException.class)
                .cause().isInstanceOf(IllegalArgumentException.class)
                .hasMessage("File backup service for gameProviderId not found: nonExistentGameProviderId1");
    }

    @Test
    void downloadFileShouldThrowIfIOExceptionOccurs() throws IOException {
        GameProviderId gameProviderId = gameProviderFileBackupService.getGameProviderId();
        GameFile gameFile = TestGameFile.gogBuilder()
                .fileSource(TestFileSource.minimalGogBuilder()
                        .gameProviderId(gameProviderId)
                        .build())
                .build();
        FileCopy fileCopy = TestFileCopy.discovered();
        mockFileBackupThrowsIOException();

        assertThatThrownBy(() -> fileBackupService.backUpFile(gameFile, fileCopy))
                .isInstanceOf(FileBackupFailedException.class)
                .cause().isInstanceOf(IOException.class);
        assertThat(fileCopy)
                .satisfies(it -> assertSoftly(softly -> {
                    softly.assertThat(it.getStatus()).isEqualTo(FileBackupStatus.FAILED);
                    softly.assertThat(it.getFailedReason()).isEqualTo("Unknown error");
                }));
    }

    private void mockFileBackupThrowsIOException() throws IOException {
        doThrow(new IOException())
                .when(gameProviderFileBackupService).backUpFile(any(), any(), any());
    }

    @Test
    void isReadyForShouldReturnTrueIfFileIsReadyToDownload() {
        GameFile gameFile = TestGameFile.gog();
        when(gameProviderFileBackupService.isReady())
                .thenReturn(true);

        assertThat(fileBackupService.isReadyFor(gameFile)).isTrue();
    }

    @Test
    void isReadyForShouldReturnFalseIfFileIsNotReadyToDownload() {
        GameFile gameFile = TestGameFile.gog();
        when(gameProviderFileBackupService.isReady())
                .thenReturn(false);

        assertThat(fileBackupService.isReadyFor(gameFile)).isFalse();
    }

    private record PersistedChanges(
            List<FileBackupStatus> savedFileBackupStatuses,
            List<String> savedFilePaths
    ) {

        private PersistedChanges() {
            this(new ArrayList<>(), new ArrayList<>());
        }

        public void addFor(FileCopy fileCopy) {
            savedFileBackupStatuses.add(fileCopy.getStatus());
            savedFilePaths.add(fileCopy.getFilePath());
        }
    }
}