package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static dev.codesoapbox.backity.core.gamefile.domain.TestGameFile.discoveredGameFile;
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
        GameFile gameFile = discoveredGameFile().build();
        AtomicBoolean gameFileWasKeptAsReferenceDuringProcessing = new AtomicBoolean();
        when(gameFileRepository.findOldestWaitingForDownload())
                .thenReturn(Optional.of(gameFile));
        when(fileBackupService.isReadyFor(gameFile))
                .thenReturn(true);
        doAnswer(inv -> {
            gameFileWasKeptAsReferenceDuringProcessing.set(
                    backUpOldestGameFileUseCase.enqueuedFileBackupReference.get() == gameFile);
            return null;
        }).when(fileBackupService).backUpFile(gameFile);

        backUpOldestGameFileUseCase.backUpOldestGameFile();

        verify(fileBackupService).backUpFile(gameFile);
        assertThat(backUpOldestGameFileUseCase.enqueuedFileBackupReference.get()).isNull();
        assertThat(gameFileWasKeptAsReferenceDuringProcessing).isTrue();
    }

    @Test
    void shouldFailGracefully() {
        GameFile gameFile = discoveredGameFile().build();

        when(gameFileRepository.findOldestWaitingForDownload())
                .thenReturn(Optional.of(gameFile));
        when(fileBackupService.isReadyFor(gameFile))
                .thenReturn(true);
        doThrow(new RuntimeException("someFailedReason"))
                .when(fileBackupService).backUpFile(gameFile);

        backUpOldestGameFileUseCase.backUpOldestGameFile();

        assertThat(backUpOldestGameFileUseCase.enqueuedFileBackupReference.get()).isNull();
        verifyNoMoreInteractions(fileBackupService);
    }

    @Test
    void shouldDoNothingIfGameProviderFileBackupServiceNotReady() {
        GameFile gameFile = discoveredGameFile().build();

        when(gameFileRepository.findOldestWaitingForDownload())
                .thenReturn(Optional.of(gameFile));
        when(fileBackupService.isReadyFor(gameFile))
                .thenReturn(false);

        backUpOldestGameFileUseCase.backUpOldestGameFile();

        verifyNoMoreInteractions(fileBackupService);
    }

    @Test
    void shouldDoNothingIfCurrentlyDownloading() {
        GameFile gameFile = discoveredGameFile().build();
        lenient().when(gameFileRepository.findOldestWaitingForDownload())
                .thenReturn(Optional.of(gameFile));

        backUpOldestGameFileUseCase.enqueuedFileBackupReference.set(gameFile);
        backUpOldestGameFileUseCase.backUpOldestGameFile();

        verifyNoInteractions(gameFileRepository, fileBackupService);
    }
}