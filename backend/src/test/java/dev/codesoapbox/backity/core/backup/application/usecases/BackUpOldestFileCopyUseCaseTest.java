package dev.codesoapbox.backity.core.backup.application.usecases;

import dev.codesoapbox.backity.core.backup.application.FileBackupContext;
import dev.codesoapbox.backity.core.backup.application.FileBackupContextFactory;
import dev.codesoapbox.backity.core.backup.application.FileBackupService;
import dev.codesoapbox.backity.core.backup.application.TestFileBackupContext;
import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProcess;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BackUpOldestFileCopyUseCaseTest {

    @InjectMocks
    private BackUpOldestFileCopyUseCase backUpOldestFileCopyUseCase;

    @Mock
    private FileCopyReplicationProcess fileCopyReplicationProcess;

    @Mock
    private FileCopyRepository fileCopyRepository;

    @Mock
    private FileBackupContextFactory fileBackupContextFactory;

    @Mock
    private FileBackupService fileBackupService;

    @Test
    void shouldDoNothingGivenFileCopyReplicationProcessNotReady() {
        fileCopyReplicationProcessIsNotReady();

        backUpOldestFileCopyUseCase.backUpOldestFileCopy();

        verifyNoInteractions(fileCopyRepository, fileBackupContextFactory, fileBackupService);
    }

    private void fileCopyReplicationProcessIsNotReady() {
        when(fileCopyReplicationProcess.canStart())
                .thenReturn(false);
    }

    @Test
    void shouldBackUpEnqueuedGameFileIfNotCurrentlyDownloading() {
        fileCopyReplicationProcessIsReady();
        FileBackupContext context = fileCopyHasContext();
        mockIsNextInQueue(context.fileCopy());

        backUpOldestFileCopyUseCase.backUpOldestFileCopy();

        verify(fileBackupService).backUpFile(context);
    }

    private void mockIsNextInQueue(FileCopy fileCopy) {
        when(fileCopyRepository.findOldestEnqueued())
                .thenReturn(Optional.of(fileCopy));
    }

    @Test
    void shouldMarkFileCopyReplicationProcessAsCompletedAfterSuccessfulBackup() {
        fileCopyReplicationProcessIsReady();
        FileBackupContext context = fileCopyHasContext();
        mockIsNextInQueue(context.fileCopy());

        backUpOldestFileCopyUseCase.backUpOldestFileCopy();

        InOrder inOrder = inOrder(fileCopyReplicationProcess, fileBackupService);
        inOrder.verify(fileBackupService).backUpFile(any());
        inOrder.verify(fileCopyReplicationProcess).markAsCompleted();
    }

    private FileBackupContext fileCopyHasContext() {
        FileBackupContext context = TestFileBackupContext.trackedLocalGog();
        when(fileBackupContextFactory.create(context.fileCopy()))
                .thenReturn(context);
        return context;
    }

    private void fileCopyReplicationProcessIsReady() {
        when(fileCopyReplicationProcess.canStart())
                .thenReturn(true);
    }

    @Test
    void shouldMarkFileCopyReplicationProcessAsCompletedAfterFailedBackup() {
        fileCopyReplicationProcessIsReady();
        FileBackupContext context = fileCopyHasContext();
        mockIsNextInQueue(context.fileCopy());
        backupServiceThrows();

        backUpOldestFileCopyUseCase.backUpOldestFileCopy();

        InOrder inOrder = inOrder(fileCopyReplicationProcess, fileBackupService);
        inOrder.verify(fileBackupService).backUpFile(any());
        inOrder.verify(fileCopyReplicationProcess).markAsCompleted();
    }

    private void backupServiceThrows() {
        doThrow(new RuntimeException("someFailedReason"))
                .when(fileBackupService).backUpFile(any());
    }
}