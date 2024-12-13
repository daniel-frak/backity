package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.domain.BackupProgress;
import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;

import java.io.IOException;

public interface GameProviderFileBackupService {

    GameProviderId getGameProviderId();

    /**
     * @return the path of the downloaded file
     */
    String backUpFile(GameFile gameFile, String tempFilePath, BackupProgress backupProgress) throws IOException;

    boolean isReady();
}