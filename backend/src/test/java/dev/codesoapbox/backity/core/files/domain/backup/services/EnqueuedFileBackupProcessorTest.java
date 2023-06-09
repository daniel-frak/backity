package dev.codesoapbox.backity.core.files.domain.backup.services;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersionBackup;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileVersionBackupRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnqueuedFileBackupProcessorTest {

    @InjectMocks
    private EnqueuedFileBackupProcessor enqueuedFileBackupProcessor;

    @Mock
    private GameFileVersionBackupRepository gameFileVersionBackupRepository;

    @Mock
    private FileBackupService fileBackupService;

    @Mock
    private FileBackupMessageService messageService;

    @Test
    void shouldProcessEnqueuedFileDownloadIfNotCurrentlyDownloading() {
        var gameFileVersionBackup = GameFileVersionBackup.builder().build();

        when(gameFileVersionBackupRepository.findOldestWaitingForDownload())
                .thenReturn(Optional.of(gameFileVersionBackup));
        when(fileBackupService.isReadyFor(gameFileVersionBackup))
                .thenReturn(true);

        enqueuedFileBackupProcessor.processQueue();

        verify(messageService).sendBackupStarted(gameFileVersionBackup);
        verify(fileBackupService).backUpGameFile(gameFileVersionBackup);
        verify(messageService).sendBackupFinished(gameFileVersionBackup);
        assertNull(enqueuedFileBackupProcessor.enqueuedFileBackupReference.get());
    }

    @Test
    void shouldFailGracefully() {
        var gameFileVersionBackup = GameFileVersionBackup.builder().build();

        when(gameFileVersionBackupRepository.findOldestWaitingForDownload())
                .thenReturn(Optional.of(gameFileVersionBackup));
        when(fileBackupService.isReadyFor(gameFileVersionBackup))
                .thenReturn(true);
        doThrow(new RuntimeException("someFailedReason"))
                .when(fileBackupService).backUpGameFile(gameFileVersionBackup);

        enqueuedFileBackupProcessor.processQueue();

        verify(messageService).sendBackupStarted(gameFileVersionBackup);
        verify(messageService).sendBackupFinished(gameFileVersionBackup);
        assertNull(enqueuedFileBackupProcessor.enqueuedFileBackupReference.get());
        verifyNoMoreInteractions(messageService, fileBackupService);
    }

    @Test
    void shouldDoNothingIfSourceDownloaderNotReady() {
        var gameFileVersionBackup = GameFileVersionBackup.builder().build();

        when(gameFileVersionBackupRepository.findOldestWaitingForDownload())
                .thenReturn(Optional.of(gameFileVersionBackup));
        when(fileBackupService.isReadyFor(gameFileVersionBackup))
                .thenReturn(false);

        enqueuedFileBackupProcessor.processQueue();

        verifyNoMoreInteractions(messageService, fileBackupService);
    }

    @Test
    void shouldDoNothingIfCurrentlyDownloading() {
        enqueuedFileBackupProcessor.enqueuedFileBackupReference.set(new GameFileVersionBackup());

        enqueuedFileBackupProcessor.processQueue();

        verifyNoInteractions(messageService, fileBackupService);
    }
}