package dev.codesoapbox.backity.core.backup.application.usecases;

import dev.codesoapbox.backity.core.backup.application.FileBackupService;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BackUpOldestFileCopyUseCaseTest {

    @InjectMocks
    private BackUpOldestFileCopyUseCase backUpOldestFileCopyUseCase;

    @Mock
    private GameFileRepository gameFileRepository;

    @Mock
    private FileCopyRepository fileCopyRepository;

    @Mock
    private FileBackupService fileBackupService;

    @Test
    void shouldDoNothingGivenAlreadyInProgress() {
        backUpOldestFileCopyUseCase.enqueuedFileCopyReference.set(TestFileCopy.discovered());

        backUpOldestFileCopyUseCase.backUpOldestFileCopy();

        verifyNoInteractions(fileCopyRepository, gameFileRepository, fileBackupService);
    }

    @Test
    void shouldBackUpEnqueuedGameFileIfNotCurrentlyDownloading() {
        GameFile gameFile = TestGameFile.gog();
        FileCopy fileCopy = TestFileCopy.discovered();
        mockIsNextInQueue(gameFile, fileCopy);
        mockBackupServiceIsReadyFor(gameFile);
        AtomicBoolean gameFileWasKeptAsReferenceDuringProcessing =
                watchFileCopyWasKeptAsReferenceDuringProcessing(fileCopy);

        backUpOldestFileCopyUseCase.backUpOldestFileCopy();

        verify(fileBackupService).backUpFile(gameFile, fileCopy);
        assertThat(backUpOldestFileCopyUseCase.enqueuedFileCopyReference.get()).isNull();
        assertThat(gameFileWasKeptAsReferenceDuringProcessing).isTrue();
    }

    private void mockBackupServiceIsReadyFor(GameFile gameFile) {
        when(fileBackupService.isReadyFor(gameFile))
                .thenReturn(true);
    }

    private void mockIsNextInQueue(GameFile gameFile, FileCopy fileCopy) {
        when(fileCopyRepository.findOldestEnqueued())
                .thenReturn(Optional.of(fileCopy));
        when(gameFileRepository.getById(fileCopy.getNaturalId().gameFileId()))
                .thenReturn(gameFile);
    }

    private AtomicBoolean watchFileCopyWasKeptAsReferenceDuringProcessing(FileCopy fileCopy) {
        AtomicBoolean gameFileWasKeptAsReferenceDuringProcessing = new AtomicBoolean();
        doAnswer(inv -> {
            gameFileWasKeptAsReferenceDuringProcessing.set(
                    backUpOldestFileCopyUseCase.enqueuedFileCopyReference.get() == fileCopy);
            return null;
        }).when(fileBackupService).backUpFile(any(), eq(fileCopy));
        return gameFileWasKeptAsReferenceDuringProcessing;
    }

    @Test
    void shouldMarkAsFailedGracefully() {
        GameFile gameFile = TestGameFile.gog();
        FileCopy fileCopy = TestFileCopy.discovered();
        mockIsNextInQueue(gameFile, fileCopy);
        mockBackupServiceIsReadyFor(gameFile);
        mockBackupServiceFails();

        backUpOldestFileCopyUseCase.backUpOldestFileCopy();

        assertThat(backUpOldestFileCopyUseCase.enqueuedFileCopyReference.get()).isNull();
        verifyNoMoreInteractions(fileBackupService);
    }

    private void mockBackupServiceFails() {
        doThrow(new RuntimeException("someFailedReason"))
                .when(fileBackupService).backUpFile(any(), any());
    }

    @Test
    void shouldDoNothingIfGameProviderFileBackupServiceNotReady() {
        GameFile gameFile = TestGameFile.gog();
        FileCopy fileCopy = TestFileCopy.discovered();
        mockIsNextInQueue(gameFile, fileCopy);
        mockBackupServiceIsNotReadyFor(gameFile);

        backUpOldestFileCopyUseCase.backUpOldestFileCopy();

        verifyNoMoreInteractions(fileBackupService);
    }

    private void mockBackupServiceIsNotReadyFor(GameFile gameFile) {
        when(fileBackupService.isReadyFor(gameFile))
                .thenReturn(false);
    }

    @Test
    void shouldDoNothingIfCurrentlyDownloading() {
        FileCopy fileCopy = TestFileCopy.discovered();

        backUpOldestFileCopyUseCase.enqueuedFileCopyReference.set(fileCopy);
        backUpOldestFileCopyUseCase.backUpOldestFileCopy();

        verifyNoInteractions(gameFileRepository, fileBackupService);
    }
}