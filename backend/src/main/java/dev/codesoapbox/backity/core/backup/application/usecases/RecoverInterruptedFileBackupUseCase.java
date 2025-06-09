package dev.codesoapbox.backity.core.backup.application.usecases;

import dev.codesoapbox.backity.core.backup.domain.events.BackupRecoveryCompletedEvent;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionRepository;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@RequiredArgsConstructor
public class RecoverInterruptedFileBackupUseCase {

    private final FileCopyRepository fileCopyRepository;
    private final BackupTargetRepository backupTargetRepository;
    private final StorageSolutionRepository storageSolutionRepository;
    private final DomainEventPublisher domainEventPublisher;

    public void recoverInterruptedFileBackup() {
        List<FileCopy> inProgressFileCopies = fileCopyRepository.findAllInProgress();
        Set<BackupTargetId> backupTargetIds = inProgressFileCopies.stream()
                .map(fileCopy -> fileCopy.getNaturalId().backupTargetId())
                .collect(toSet());
        Map<BackupTargetId, BackupTarget> backupTargets = backupTargetRepository.findAllByIdIn(backupTargetIds).stream()
                .collect(toMap(BackupTarget::getId, identity()));
        Map<StorageSolutionId, StorageSolution> storageSolutionsById = storageSolutionRepository.findAll().stream()
                .collect(toMap(StorageSolution::getId, identity()));

        for (FileCopy fileCopy : inProgressFileCopies) {
            BackupTarget backupTarget = backupTargets.get(fileCopy.getNaturalId().backupTargetId());
            StorageSolution storageSolution = storageSolutionsById.get(backupTarget.getStorageSolutionId());
            storageSolution.deleteIfExists(fileCopy.getFilePath());
            fileCopy.toFailed("Backup was interrupted before completion", null);
            fileCopyRepository.save(fileCopy);
        }

        domainEventPublisher.publish(new BackupRecoveryCompletedEvent());
    }
}
