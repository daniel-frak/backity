package dev.codesoapbox.backity.core.backup.application.usecases;

import dev.codesoapbox.backity.core.backup.application.FileBackupService;
import dev.codesoapbox.backity.core.backup.domain.events.BackupRecoveryCompletedEvent;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.backuptarget.domain.TestBackupTarget;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionRepository;
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
    private BackupTargetRepository backupTargetRepository;

    @Mock
    private StorageSolutionRepository storageSolutionRepository;

    @Mock
    private FileBackupService fileBackupService;

    @Test
    void shouldDoNothingGivenAlreadyInProgress() {
        backUpOldestFileCopyUseCase.enqueuedFileCopyReference.set(TestFileCopy.tracked());

        backUpOldestFileCopyUseCase.backUpOldestFileCopy();

        verifyNoInteractions(fileCopyRepository, gameFileRepository, fileBackupService);
    }

    @Test
    void shouldBackUpEnqueuedGameFileIfNotCurrentlyDownloading() {
        backUpOldestFileCopyUseCase.handle(new BackupRecoveryCompletedEvent());
        FileCopy fileCopy = TestFileCopy.tracked();
        GameFile gameFile = TestGameFile.gog();
        mockIsNextInQueue(gameFile, fileCopy);
        BackupTarget backupTarget = mockBackupTargetExists(fileCopy);
        StorageSolution storageSolution = mockStorageSolutionExists(backupTarget);
        AtomicBoolean fileCopyWasKeptAsReferenceDuringProcessing =
                watchFileCopyWasKeptAsReferenceDuringProcessing(fileCopy);

        backUpOldestFileCopyUseCase.backUpOldestFileCopy();

        verify(fileBackupService).backUpFile(fileCopy, gameFile, backupTarget, storageSolution);
        assertThat(backUpOldestFileCopyUseCase.enqueuedFileCopyReference.get()).isNull();
        assertThat(fileCopyWasKeptAsReferenceDuringProcessing).isTrue();
    }

    private BackupTarget mockBackupTargetExists(FileCopy fileCopy) {
        BackupTarget backupTarget = TestBackupTarget.localFolder();
        when(backupTargetRepository.getById(fileCopy.getNaturalId().backupTargetId()))
                .thenReturn(backupTarget);
        return backupTarget;
    }

    private StorageSolution mockStorageSolutionExists(BackupTarget backupTarget) {
        StorageSolution storageSolution = mock(StorageSolution.class);
        when(storageSolutionRepository.getById(backupTarget.getStorageSolutionId()))
                .thenReturn(storageSolution);
        return storageSolution;
    }

    private void mockIsNextInQueue(GameFile gameFile, FileCopy fileCopy) {
        when(fileCopyRepository.findOldestEnqueued())
                .thenReturn(Optional.of(fileCopy));
        when(gameFileRepository.getById(fileCopy.getNaturalId().gameFileId()))
                .thenReturn(gameFile);
    }

    private AtomicBoolean watchFileCopyWasKeptAsReferenceDuringProcessing(FileCopy fileCopy) {
        var fileCopyWasKeptAsReferenceDuringProcessing = new AtomicBoolean();
        doAnswer(inv -> {
            fileCopyWasKeptAsReferenceDuringProcessing.set(
                    backUpOldestFileCopyUseCase.enqueuedFileCopyReference.get() == fileCopy);
            return null;
        }).when(fileBackupService).backUpFile(eq(fileCopy), any(), any(), any());
        return fileCopyWasKeptAsReferenceDuringProcessing;
    }

    @Test
    void shouldMarkAsFailedGracefully() {
        backUpOldestFileCopyUseCase.handle(new BackupRecoveryCompletedEvent());
        FileCopy fileCopy = TestFileCopy.tracked();
        GameFile gameFile = TestGameFile.gog();
        mockIsNextInQueue(gameFile, fileCopy);
        BackupTarget backupTarget = mockBackupTargetExists(fileCopy);
        mockStorageSolutionExists(backupTarget);
        mockBackupServiceFails();

        backUpOldestFileCopyUseCase.backUpOldestFileCopy();

        assertThat(backUpOldestFileCopyUseCase.enqueuedFileCopyReference.get()).isNull();
        verifyNoMoreInteractions(fileBackupService);
    }

    private void mockBackupServiceFails() {
        doThrow(new RuntimeException("someFailedReason"))
                .when(fileBackupService).backUpFile(any(), any(), any(), any());
    }

    @Test
    void shouldDoNothingIfCurrentlyDownloading() {
        backUpOldestFileCopyUseCase.handle(new BackupRecoveryCompletedEvent());
        FileCopy fileCopy = TestFileCopy.tracked();

        backUpOldestFileCopyUseCase.enqueuedFileCopyReference.set(fileCopy);
        backUpOldestFileCopyUseCase.backUpOldestFileCopy();

        verifyNoInteractions(fileCopyRepository, gameFileRepository, backupTargetRepository, storageSolutionRepository,
                fileBackupService);
    }

    @Test
    void shouldDoNothingIfBackupRecoveryNotComplete() {
        backUpOldestFileCopyUseCase.backUpOldestFileCopy();

        verifyNoInteractions(fileCopyRepository, gameFileRepository, backupTargetRepository, storageSolutionRepository,
                fileBackupService);
    }
}