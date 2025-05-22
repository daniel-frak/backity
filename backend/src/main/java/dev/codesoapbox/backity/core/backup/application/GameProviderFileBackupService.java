package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgress;
import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;

import java.io.IOException;

public interface GameProviderFileBackupService {

    GameProviderId getGameProviderId();

    void backUpFile(StorageSolution storageSolution, GameFile gameFile, FileCopy fileCopy,
                    DownloadProgress downloadProgress) throws IOException;

    boolean isReady();
}
