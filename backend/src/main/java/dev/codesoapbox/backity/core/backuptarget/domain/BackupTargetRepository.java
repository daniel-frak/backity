package dev.codesoapbox.backity.core.backuptarget.domain;

import java.util.Collection;
import java.util.List;

public interface BackupTargetRepository {

    void save(BackupTarget backupTarget);

    BackupTarget getById(BackupTargetId backupTargetId);

    List<BackupTarget> findAll();

    List<BackupTarget> findAllByIdIn(Collection<BackupTargetId> ids);
}
