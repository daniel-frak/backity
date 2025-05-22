package dev.codesoapbox.backity.core.backuptarget.adapters.driven.persistence.hardcoded;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.backuptarget.domain.exceptions.BackupTargetNotFoundException;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * A temporary solution until BackupTarget CRUD is implemented.
 */
@RequiredArgsConstructor
public class HardCodedBackupTargetRepository implements BackupTargetRepository {

    private final BackupTarget availableBackupTarget;

    public HardCodedBackupTargetRepository(boolean s3Enabled, @NonNull String pathTemplate) {
        if (s3Enabled) {
            availableBackupTarget = new BackupTarget(
                    new BackupTargetId("d46dde81-e519-4300-9a54-6f9e7d637926"),
                    "S3 bucket",
                    new StorageSolutionId("S3"),
                    pathTemplate
            );
        } else {
            availableBackupTarget = new BackupTarget(
                    new BackupTargetId("224440e2-6e5c-4f24-94ac-3222587652f7"),
                    "Local folder",
                    new StorageSolutionId("LOCAL_FILE_SYSTEM"),
                    pathTemplate
            );
        }
    }

    @Override
    public List<BackupTarget> findAllBackupTargets() {
        return List.of(availableBackupTarget);
    }

    @Override
    public BackupTarget getById(BackupTargetId backupTargetId) {
        if (availableBackupTarget.getId().equals(backupTargetId)) {
            return availableBackupTarget;
        }
        throw new BackupTargetNotFoundException(backupTargetId);
    }
}
