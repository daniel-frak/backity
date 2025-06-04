package dev.codesoapbox.backity.core.backuptarget.domain;

import java.util.Collection;
import java.util.List;

public interface BackupTargetRepository {

    List<BackupTarget> findAll();

    BackupTarget getById(BackupTargetId backupTargetId);

    List<BackupTarget> findAllByIdIn(Collection<BackupTargetId> ids);
}
