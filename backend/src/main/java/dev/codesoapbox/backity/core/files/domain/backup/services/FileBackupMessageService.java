package dev.codesoapbox.backity.core.files.domain.backup.services;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersion;
import dev.codesoapbox.backity.core.files.domain.backup.model.messages.FileBackupProgress;

public interface FileBackupMessageService {

    void sendBackupStarted(GameFileVersion payload);

    void sendProgress(FileBackupProgress payload);

    void sendBackupFinished(GameFileVersion payload);
}
