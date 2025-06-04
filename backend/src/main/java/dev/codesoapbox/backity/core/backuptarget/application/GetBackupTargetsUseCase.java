package dev.codesoapbox.backity.core.backuptarget.application;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetBackupTargetsUseCase {

    private final BackupTargetRepository backupTargetRepository;

    public List<BackupTarget> getBackupTargets() {
        return backupTargetRepository.findAll();
    }
}
