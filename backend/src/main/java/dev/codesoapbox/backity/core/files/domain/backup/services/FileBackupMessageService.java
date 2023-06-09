package dev.codesoapbox.backity.core.files.domain.backup.services;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersionBackup;
import dev.codesoapbox.backity.core.files.domain.backup.model.messages.FileBackupProgress;

public interface FileBackupMessageService {

    void sendBackupStarted(GameFileVersionBackup payload);

    void sendProgress(FileBackupProgress payload);

    void sendBackupFinished(GameFileVersionBackup payload);
}
