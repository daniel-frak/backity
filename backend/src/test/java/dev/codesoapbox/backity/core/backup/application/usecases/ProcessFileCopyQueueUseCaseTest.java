package dev.codesoapbox.backity.core.backup.application.usecases;

import dev.codesoapbox.backity.core.backup.application.FileBackupContext;
import dev.codesoapbox.backity.core.backup.application.FileBackupContextFactory;
import dev.codesoapbox.backity.core.backup.application.FileBackupService;
import dev.codesoapbox.backity.core.backup.application.TestFileBackupContext;
import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProcess;
import dev.codesoapbox.backity.core.filecopy.domain.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessFileCopyQueueUseCaseTest {

    @InjectMocks
    private ProcessFileCopyQueueUseCase processFileCopyQueueUseCase;

    @Mock
    private FileCopyReplicationProcess fileCopyReplicationProcess;

    @Mock
    private FileCopyRepository fileCopyRepository;

    @Mock
    private FileBackupContextFactory fileBackupContextFactory;

    @Mock
    private FileBackupService fileBackupService;

    @Nested
    // Timeout for safety due to potentially infinite while loop:
    @Timeout(value = 5, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    class ProcessFileCopyQueue {

        @Test
        void shouldDoNothingGivenFileCopyReplicationProcessNotReady() {
            fileCopyReplicationProcessIsNotReady();

            processFileCopyQueueUseCase.processFileCopyQueue();

            verifyNoInteractions(fileCopyRepository, fileBackupContextFactory, fileBackupService);
        }

        private void fileCopyReplicationProcessIsNotReady() {
            when(fileCopyReplicationProcess.tryStart())
                    .thenReturn(false);
        }

        @Test
        void shouldBackUpEnqueuedFileCopyGivenNotCurrentlyDownloading() {
            fileCopyReplicationProcessIsReady();
            FileBackupContext context = TestFileBackupContext.trackedLocalGog();
            exists(context);
            queueContains(List.of(context.fileCopy()));

            processFileCopyQueueUseCase.processFileCopyQueue();

            verify(fileBackupService, times(1)).backUpFile(context);
        }

        private void queueContains(List<FileCopy> fileCopies) {
            Queue<Optional<FileCopy>> responseQueue = new ArrayDeque<>();
            for (FileCopy fileCopy : fileCopies) {
                responseQueue.add(Optional.of(fileCopy));
            }

            when(fileCopyRepository.findOldestEnqueued())
                    .thenAnswer(_ -> Optional.ofNullable(responseQueue.poll())
                            .orElseGet(Optional::empty));
        }

        @Test
        void shouldBackUpEnqueuedFileCopiesUntilQueueIsEmpty() {
            fileCopyReplicationProcessIsReady();
            FileBackupContext context1 = contextForUniqueFileCopy1();
            FileBackupContext context2 = contextForUniqueFileCopy2();
            exist(context1, context2);
            queueContains(List.of(context1.fileCopy(), context2.fileCopy()));

            processFileCopyQueueUseCase.processFileCopyQueue();

            verify(fileBackupService, times(1)).backUpFile(context1);
            verify(fileBackupService, times(1)).backUpFile(context2);
        }

        @Test
        void shouldStopProcessingGivenEncountersPreviousFileCopy() {
            fileCopyReplicationProcessIsReady();
            FileBackupContext context1 = contextForUniqueFileCopy1();
            FileBackupContext context2 = contextForUniqueFileCopy2();
            exist(context1, context2);
            queueContains(List.of(context1.fileCopy(), context1.fileCopy(), context2.fileCopy()));

            processFileCopyQueueUseCase.processFileCopyQueue();

            verify(fileBackupService, times(1)).backUpFile(context1);
            verify(fileBackupService, never()).backUpFile(context2);
        }

        @Test
        void shouldProcessFileCopyGivenItIsEnqueuedAfterFinishingButBeforeMarkingAsCompleted() {
            fileCopyReplicationProcessIsReady();
            FileBackupContext context1 = contextForUniqueFileCopy1();
            FileBackupContext context2 = contextForUniqueFileCopy2();
            exist(context1, context2);

            AtomicBoolean markAsCompletedWasCalled = trackMarkAsCompletedWasCalled();
            secondFileCopyGetsEnqueuedAfterQueueProcessingMarkedAsCompleted(
                    context1, context2, markAsCompletedWasCalled);
            fileBackupServiceSuccessfullyBacksUpFile();

            processFileCopyQueueUseCase.processFileCopyQueue();

            verify(fileBackupService, times(1)).backUpFile(context1);
            verify(fileBackupService, times(1)).backUpFile(context2);
        }

        private FileBackupContext contextForUniqueFileCopy1() {
            return TestFileBackupContext.enqueuedLocalGogBuilder()
                    .fileCopy(TestFileCopy.enqueuedBuilder()
                            .id(new FileCopyId("057a134d-c2e8-49c5-a117-7abbec3ad107"))
                            .build())
                    .build();
        }

        private FileBackupContext contextForUniqueFileCopy2() {
            return TestFileBackupContext.enqueuedLocalGogBuilder()
                    .fileCopy(TestFileCopy.enqueuedBuilder()
                            .id(new FileCopyId("99dbdba6-484b-492e-a729-972b796e6cec"))
                            .build())
                    .build();
        }

        private AtomicBoolean trackMarkAsCompletedWasCalled() {
            var markAsCompletedWasCalled = new AtomicBoolean(false);
            doAnswer(_ -> {
                markAsCompletedWasCalled.set(true);
                return null;
            }).when(fileCopyReplicationProcess).markAsCompleted();
            return markAsCompletedWasCalled;
        }

        private void secondFileCopyGetsEnqueuedAfterQueueProcessingMarkedAsCompleted(
                FileBackupContext contextForEnqueuedBeforeMarkingAsCompleted, FileBackupContext contextForEnqueuedAfterMarkingAsCompleted, AtomicBoolean markAsCompletedWasCalled) {
            when(fileCopyRepository.findOldestEnqueued())
                    .thenAnswer(_ -> {
                        // Return one in the first run
                        if (contextForEnqueuedBeforeMarkingAsCompleted.fileCopy().getStatus()
                                == FileCopyStatus.ENQUEUED) {
                            return Optional.of(contextForEnqueuedBeforeMarkingAsCompleted.fileCopy());
                        }

                        // Return one after finishing first run
                        if (markAsCompletedWasCalled.get()
                                && contextForEnqueuedAfterMarkingAsCompleted.fileCopy().getStatus()
                                == FileCopyStatus.ENQUEUED) {
                            return Optional.of(contextForEnqueuedAfterMarkingAsCompleted.fileCopy());
                        }

                        return Optional.empty();
                    });
        }

        private void fileBackupServiceSuccessfullyBacksUpFile() {
            doAnswer(inv -> {
                FileCopy fileCopy = ((FileBackupContext) inv.getArgument(0)).fileCopy();
                fileCopy.toInProgress("testFilePath");
                fileCopy.toStoredIntegrityUnknown();
                return null;
            }).when(fileBackupService).backUpFile(any());
        }

        @Test
        void shouldMarkFileCopyReplicationProcessAsCompletedAfterSuccessfulBackup() {
            fileCopyReplicationProcessIsReady();
            FileBackupContext context = TestFileBackupContext.trackedLocalGog();
            exists(context);
            queueContains(List.of(context.fileCopy()));

            processFileCopyQueueUseCase.processFileCopyQueue();

            InOrder inOrder = inOrder(fileCopyReplicationProcess, fileBackupService);
            inOrder.verify(fileBackupService).backUpFile(any());
            inOrder.verify(fileCopyReplicationProcess).markAsCompleted();
        }

        private void exists(FileBackupContext context) {
            when(fileBackupContextFactory.create(context.fileCopy()))
                    .thenReturn(context);
        }

        private void exist(FileBackupContext... contexts) {
            for (FileBackupContext context : contexts) {
                lenient().when(fileBackupContextFactory.create(context.fileCopy()))
                        .thenReturn(context);
            }
        }

        private void fileCopyReplicationProcessIsReady() {
            when(fileCopyReplicationProcess.tryStart())
                    .thenReturn(true);
        }

        @Test
        void shouldMarkFileCopyReplicationProcessAsCompletedAfterFailedBackup() {
            fileCopyReplicationProcessIsReady();
            FileBackupContext context = TestFileBackupContext.trackedLocalGog();
            exists(context);
            queueContains(List.of(context.fileCopy()));
            backupServiceThrows();

            processFileCopyQueueUseCase.processFileCopyQueue();

            InOrder inOrder = inOrder(fileCopyReplicationProcess, fileBackupService);
            inOrder.verify(fileBackupService).backUpFile(any());
            inOrder.verify(fileCopyReplicationProcess).markAsCompleted();
        }

        private void backupServiceThrows() {
            doThrow(new RuntimeException("someFailedReason"))
                    .when(fileBackupService).backUpFile(any());
        }

        @Test
        void shouldCompleteProcessGivenQueueIsEmpty() {
            fileCopyReplicationProcessIsReady();
            queueIsEmpty();

            processFileCopyQueueUseCase.processFileCopyQueue();

            verify(fileCopyReplicationProcess).markAsCompleted();
            verifyNoInteractions(fileBackupContextFactory, fileBackupService);
        }

        private void queueIsEmpty() {
            when(fileCopyRepository.findOldestEnqueued()).thenReturn(Optional.empty());
        }
    }
}
