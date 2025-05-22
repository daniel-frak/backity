package dev.codesoapbox.backity.core.backuptarget.adapters.driven.hardcoded;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;

import java.util.List;

public class HardCodedBackupTargetRepository implements BackupTargetRepository {

    @Override
    public List<BackupTarget> findAllBackupTargets() {
        // @TODO Return either for LocalFileSystem or S3 and TEST
        return List.of();
    }
}
