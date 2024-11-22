package dev.codesoapbox.backity.core.backup.domain;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;

public interface FileBackupEventPublisher {

    void publishBackupStartedEvent(GameFile gameFile);

    void publishFileBackupProgressChangedEvent(FileBackupProgress fileBackupProgress);

    void publishBackupFinishedEvent(GameFile gameFile);
}
