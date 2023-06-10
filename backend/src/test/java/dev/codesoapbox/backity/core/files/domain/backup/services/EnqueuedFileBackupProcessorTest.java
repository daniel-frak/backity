package dev.codesoapbox.backity.core.files.domain.backup.services;

import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetailsId;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileDetailsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnqueuedFileBackupProcessorTest {

    @InjectMocks
    private EnqueuedFileBackupProcessor enqueuedFileBackupProcessor;

    @Mock
    private GameFileDetailsRepository gameFileDetailsRepository;

    @Mock
    private FileBackupService fileBackupService;

    @Mock
    private FileBackupMessageService messageService;

    @Test
    void shouldProcessEnqueuedFileDownloadIfNotCurrentlyDownloading() {
        var gameFileVersionBackup = new GameFileDetails(
                new GameFileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48")),
                "someSource", "someUrl", "someTitle", "someOriginalFileName",
                null, null, "someGameId", "someVersion", "100 KB",
                null, null, FileBackupStatus.DISCOVERED, null);

        when(gameFileDetailsRepository.findOldestWaitingForDownload())
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
        var gameFileVersionBackup = new GameFileDetails(
                new GameFileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48")),
                "someSource", "someUrl", "someTitle", "someOriginalFileName",
                null, null, "someGameId", "someVersion", "100 KB",
                null, null, FileBackupStatus.DISCOVERED, null);

        when(gameFileDetailsRepository.findOldestWaitingForDownload())
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
        var gameFileVersionBackup = new GameFileDetails(
                new GameFileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48")),
                "someSource", "someUrl", "someTitle", "someOriginalFileName",
                null, null, "someGameId", "someVersion", "100 KB",
                null, null, FileBackupStatus.DISCOVERED, null);

        when(gameFileDetailsRepository.findOldestWaitingForDownload())
                .thenReturn(Optional.of(gameFileVersionBackup));
        when(fileBackupService.isReadyFor(gameFileVersionBackup))
                .thenReturn(false);

        enqueuedFileBackupProcessor.processQueue();

        verifyNoMoreInteractions(messageService, fileBackupService);
    }

    @Test
    void shouldDoNothingIfCurrentlyDownloading() {
        var gameFileVersionBackup = new GameFileDetails(
                new GameFileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48")),
                "someSource", "someUrl", "someTitle", "someOriginalFileName",
                null, null, "someGameId", "someVersion", "100 KB",
                null, null, FileBackupStatus.DISCOVERED, null);
        enqueuedFileBackupProcessor.enqueuedFileBackupReference.set(gameFileVersionBackup);

        enqueuedFileBackupProcessor.processQueue();

        verifyNoInteractions(messageService, fileBackupService);
    }
}