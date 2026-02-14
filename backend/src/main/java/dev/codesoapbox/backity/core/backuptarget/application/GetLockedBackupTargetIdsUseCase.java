package dev.codesoapbox.backity.core.backuptarget.application;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetLockedBackupTargetIdsUseCase {

    private final FileCopyRepository fileCopyRepository;

    public List<BackupTargetId> getLockedBackupTargetIds() {
        return fileCopyRepository.getUniqueBackupTargetIdsByStatusNotIn(FileCopyStatus.NON_LOCKING_STATUSES);
    }
}
