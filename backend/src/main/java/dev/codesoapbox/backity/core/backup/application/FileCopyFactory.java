package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.filecopy.domain.FileBackupStatus;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class FileCopyFactory {

    private final Supplier<FileCopyId> idSupplier;

    // @TODO Use when backing up and FileCopy is missing for given GameFileId and GameProviderId
    public FileCopy create(GameFileId gameFileId, BackupTargetId backupTargetId) {
        return new FileCopy(idSupplier.get(), gameFileId, backupTargetId, FileBackupStatus.DISCOVERED,
                null, null, null, null);
    }
}
