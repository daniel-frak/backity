package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileBackupContextFactory {

    private final GameFileRepository gameFileRepository;
    private final BackupTargetRepository backupTargetRepository;
    private final StorageSolutionRepository storageSolutionRepository;

    public FileBackupContext create(FileCopy fileCopy) {
        GameFile gameFile = gameFileRepository.getById(fileCopy.getNaturalId().gameFileId());
        BackupTarget backupTarget = backupTargetRepository.getById(fileCopy.getNaturalId().backupTargetId());
        StorageSolution storageSolution = storageSolutionRepository.getById(backupTarget.getStorageSolutionId());

        return new FileBackupContext(fileCopy, gameFile, backupTarget, storageSolution);
    }
}
