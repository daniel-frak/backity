package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.webclient;

import dev.codesoapbox.backity.core.backup.application.writeprogress.OutputStreamProgressTracker;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogGameWithFiles;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogLibraryService;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.webclient.operations.GetGameDetailsGogEmbedOperation;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.webclient.operations.GetLibraryGameIdsGogEmbedOperation;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.webclient.operations.GetLibrarySizeGogEmbedOperation;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.webclient.operations.InitializeProgressAndStreamFileGogEmbedOperation;
import dev.codesoapbox.backity.gameproviders.shared.infrastructure.adapters.driven.api.spring.DataBufferFluxTrackableFileStream;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

// https://gogapidocs.readthedocs.io/en/latest/index.html
@RequiredArgsConstructor
public class GogEmbedWebClient implements GogLibraryService {

    private final GetLibrarySizeGogEmbedOperation getLibrarySizeOperation;
    private final GetGameDetailsGogEmbedOperation getGameDetailsOperation;
    private final GetLibraryGameIdsGogEmbedOperation getLibraryGameIdsOperation;
    private final InitializeProgressAndStreamFileGogEmbedOperation initializeProgressAndStreamFileOperation;

    @Override
    public String getLibrarySize() {
        return getLibrarySizeOperation.execute();
    }

    @Override
    public Optional<GogGameWithFiles> getGameDetails(String gameId) {
        return getGameDetailsOperation.execute(gameId);
    }

    public List<String> getLibraryGameIds() {
        return getLibraryGameIdsOperation.execute();
    }

    public DataBufferFluxTrackableFileStream initializeProgressAndStreamFile(
            SourceFile sourceFile, OutputStreamProgressTracker progressTracker) {
        return initializeProgressAndStreamFileOperation.execute(sourceFile, progressTracker);
    }
}