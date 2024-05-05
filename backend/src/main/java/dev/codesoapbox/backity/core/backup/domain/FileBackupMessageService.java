package dev.codesoapbox.backity.core.backup.domain;

import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;

public interface FileBackupMessageService {

    void sendBackupStarted(FileDetails payload);

    void sendProgress(FileBackupProgress payload);

    void sendBackupFinished(FileDetails payload);
}
