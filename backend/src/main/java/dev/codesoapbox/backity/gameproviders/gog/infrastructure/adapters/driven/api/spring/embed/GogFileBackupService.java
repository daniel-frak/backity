package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed;

import dev.codesoapbox.backity.core.backup.application.TrackableFileStream;
import dev.codesoapbox.backity.core.backup.application.writeprogress.OutputStreamProgressTracker;
import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.backup.application.GameProviderFileBackupService;
import dev.codesoapbox.backity.core.gamefile.domain.FileSource;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class GogFileBackupService implements GameProviderFileBackupService {

    private final GogEmbedWebClient gogEmbedWebClient;
    private final GogAuthService authService;

    public GameProviderId getGameProviderId() {
        return new GameProviderId("GOG");
    }

    @Override
    public TrackableFileStream acquireTrackableFileStream(
            GameFile gameFile, OutputStreamProgressTracker outputStreamProgressTracker) {
        FileSource fileSource = gameFile.getFileSource();
        return gogEmbedWebClient.initializeProgressAndStreamFile(fileSource, outputStreamProgressTracker);
    }

    @Override
    public boolean isConnected() {
        return authService.isAuthenticated();
    }
}
