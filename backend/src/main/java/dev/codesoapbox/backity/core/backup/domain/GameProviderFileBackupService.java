package dev.codesoapbox.backity.core.backup.domain;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;

import java.io.IOException;

public interface GameProviderFileBackupService {

    GameProviderId getGameProviderId();

    /**
     * @return the path of the downloaded file
     */
    String backUpFile(GameFile gameFile, String tempFilePath) throws IOException;

    boolean isReady();
}
