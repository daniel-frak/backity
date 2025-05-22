package dev.codesoapbox.backity.core.backuptarget.domain;

import java.util.List;

public interface BackupTargetRepository {

    List<BackupTarget> findAllBackupTargets();

    BackupTarget getById(BackupTargetId backupTargetId);
}
