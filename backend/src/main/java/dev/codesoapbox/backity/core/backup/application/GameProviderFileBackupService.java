package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.writeprogress.OutputStreamProgressTracker;
import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;

public interface GameProviderFileBackupService {

    GameProviderId getGameProviderId();

    TrackableFileStream acquireTrackableFileStream(
            SourceFile sourceFile, OutputStreamProgressTracker outputStreamProgressTracker);

    boolean isConnected();
}
