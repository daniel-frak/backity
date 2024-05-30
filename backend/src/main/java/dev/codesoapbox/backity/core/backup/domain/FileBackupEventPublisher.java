package dev.codesoapbox.backity.core.backup.domain;

import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;

public interface FileBackupEventPublisher {

    void publishBackupStartedEvent(FileDetails payload);

    void publishFileBackupProgressChangedEvent(FileBackupProgress payload);

    void publishBackupFinishedEvent(FileDetails payload);
}
