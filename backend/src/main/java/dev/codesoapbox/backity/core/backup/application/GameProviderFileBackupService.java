package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgress;
import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;

public interface GameProviderFileBackupService {

    GameProviderId getGameProviderId();

    TrackableFileStream acquireTrackableFileStream(GameFile gameFile,
                                                   DownloadProgress downloadProgress);

    boolean isConnected();
}
