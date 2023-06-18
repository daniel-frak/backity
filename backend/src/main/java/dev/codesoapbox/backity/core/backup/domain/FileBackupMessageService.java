package dev.codesoapbox.backity.core.backup.domain;

import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetails;

public interface FileBackupMessageService {

    void sendBackupStarted(GameFileDetails payload);

    void sendProgress(FileBackupProgress payload);

    void sendBackupFinished(GameFileDetails payload);
}
