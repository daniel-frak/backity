package dev.codesoapbox.backity.gameproviders.gog.application;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgress;
import dev.codesoapbox.backity.core.gamefile.domain.GameProviderFile;

public interface GogFileProvider {

    TrackableFileStream initializeProgressAndStreamFile(GameProviderFile gameProviderFile, DownloadProgress progress);
}
