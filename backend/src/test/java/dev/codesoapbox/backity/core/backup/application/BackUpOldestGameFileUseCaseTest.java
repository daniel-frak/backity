package dev.codesoapbox.backity.core.backup.application;

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
class BackUpOldestGameFileUseCaseTest {

    @InjectMocks
    private BackUpOldestGameFileUseCase backUpOldestGameFileUseCase;

    @Mock
    private GameFileRepository gameFileRepository;

    @Mock
    private FileBackupService fileBackupService;

    @Test
    void shouldBackUpEnqueuedGameFileIfNotCurrentlyDownloading() {
        GameFile gameFile = TestGameFile.discovered();
        mockIsNextInQueue(gameFile);
        mockBackupServiceIsReadyFor(gameFile);
        AtomicBoolean gameFileWasKeptAsReferenceDuringProcessing =
                watchGameFileWasKeptAsReferenceDuringProcessing(gameFile);

        backUpOldestGameFileUseCase.backUpOldestGameFile();

        verify(fileBackupService).backUpFile(gameFile);
        assertThat(backUpOldestGameFileUseCase.enqueuedFileBackupReference.get()).isNull();
        assertThat(gameFileWasKeptAsReferenceDuringProcessing).isTrue();
    }

    private void mockBackupServiceIsReadyFor(GameFile gameFile) {
        when(fileBackupService.isReadyFor(gameFile))
                .thenReturn(true);
    }

    private void mockIsNextInQueue(GameFile gameFile) {
        when(gameFileRepository.findOldestWaitingForDownload())
                .thenReturn(Optional.of(gameFile));
    }

    private AtomicBoolean watchGameFileWasKeptAsReferenceDuringProcessing(GameFile gameFile) {
        AtomicBoolean gameFileWasKeptAsReferenceDuringProcessing = new AtomicBoolean();
        doAnswer(inv -> {
            gameFileWasKeptAsReferenceDuringProcessing.set(
                    backUpOldestGameFileUseCase.enqueuedFileBackupReference.get() == gameFile);
            return null;
        }).when(fileBackupService).backUpFile(gameFile);
        return gameFileWasKeptAsReferenceDuringProcessing;
    }

    @Test
    void shouldFailGracefully() {
        GameFile gameFile = TestGameFile.discovered();
        mockIsNextInQueue(gameFile);
        mockBackupServiceIsReadyFor(gameFile);
        mockBackupServiceFails();

        backUpOldestGameFileUseCase.backUpOldestGameFile();

        assertThat(backUpOldestGameFileUseCase.enqueuedFileBackupReference.get()).isNull();
        verifyNoMoreInteractions(fileBackupService);
    }

    private void mockBackupServiceFails() {
        doThrow(new RuntimeException("someFailedReason"))
                .when(fileBackupService).backUpFile(any());
    }

    @Test
    void shouldDoNothingIfGameProviderFileBackupServiceNotReady() {
        GameFile gameFile = TestGameFile.discovered();
        mockIsNextInQueue(gameFile);
        mockBackupServiceIsNotReadyFor(gameFile);

        backUpOldestGameFileUseCase.backUpOldestGameFile();

        verifyNoMoreInteractions(fileBackupService);
    }

    private void mockBackupServiceIsNotReadyFor(GameFile gameFile) {
        when(fileBackupService.isReadyFor(gameFile))
                .thenReturn(false);
    }

    @Test
    void shouldDoNothingIfCurrentlyDownloading() {
        GameFile gameFile = TestGameFile.discovered();

        backUpOldestGameFileUseCase.enqueuedFileBackupReference.set(gameFile);
        backUpOldestGameFileUseCase.backUpOldestGameFile();

        verifyNoInteractions(gameFileRepository, fileBackupService);
    }
}