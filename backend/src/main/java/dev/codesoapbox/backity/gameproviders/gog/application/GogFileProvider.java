package dev.codesoapbox.backity.gameproviders.gog.application;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.BackupProgress;
import dev.codesoapbox.backity.core.gamefile.domain.GameProviderFile;

public interface GogFileProvider {

    TrackableFileStream initializeProgressAndStreamFile(GameProviderFile gameProviderFile, BackupProgress progress);
}
