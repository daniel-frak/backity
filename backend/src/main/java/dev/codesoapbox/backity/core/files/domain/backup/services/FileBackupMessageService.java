package dev.codesoapbox.backity.core.files.domain.backup.services;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.model.messages.FileBackupProgress;

public interface FileBackupMessageService {

    void sendBackupStarted(GameFileDetails payload);

    void sendProgress(FileBackupProgress payload);

    void sendBackupFinished(GameFileDetails payload);
}
