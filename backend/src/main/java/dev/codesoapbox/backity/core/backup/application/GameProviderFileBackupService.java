package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgress;
import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;

import java.io.IOException;

public interface GameProviderFileBackupService {

    GameProviderId getGameProviderId();

    /**
     * @return the path of the downloaded file
     */
    void backUpFile(GameFile gameFile, FileCopy fileCopy, DownloadProgress downloadProgress) throws IOException;

    boolean isReady();
}
