package dev.codesoapbox.backity.core.backup.domain;

import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;

public interface FileBackupEventPublisher {

    void publishBackupStartedEvent(FileDetails fileDetails);

    void publishFileBackupProgressChangedEvent(FileBackupProgress fileBackupProgress);

    void publishBackupFinishedEvent(FileDetails fileDetails);
}
