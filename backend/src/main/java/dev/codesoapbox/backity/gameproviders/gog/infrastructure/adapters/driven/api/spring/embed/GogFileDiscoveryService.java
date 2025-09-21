package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed;

import dev.codesoapbox.backity.core.discovery.application.GameDiscoveryProgressTracker;
import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.application.GameProviderFileDiscoveryService;
import dev.codesoapbox.backity.core.gamefile.domain.FileSource;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogGameWithFiles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class GogFileDiscoveryService implements GameProviderFileDiscoveryService {

    private final GogEmbedWebClient gogEmbedWebClient;
    private final AtomicBoolean shouldStopFileDiscovery = new AtomicBoolean();
    private final GogGameWithFilesMapper gogGameWithFilesMapper;

    public GameProviderId getGameProviderId() {
        return GogGameProviderId.get();
    }

    @Override
    public void discoverAllFiles(
            Consumer<FileSource> fileSourceConsumer, GameDiscoveryProgressTracker progressTracker) {
        log.info("Discovering new files...");

        shouldStopFileDiscovery.set(false);
        List<String> libraryGameIds = gogEmbedWebClient.getLibraryGameIds();
        int totalGames = libraryGameIds.size();
        progressTracker.initializeGamesDiscovered(totalGames);

        libraryGameIds.stream()
                .takeWhile(_ -> !shouldStopFileDiscovery.get())
                .forEach(id -> {
                    GogGameWithFiles details = gogEmbedWebClient.getGameDetails(id);
                    processFiles(fileSourceConsumer, details);
                    progressTracker.incrementGamesDiscovered(1);
                });
    }

    private void processFiles(Consumer<FileSource> fileSourceConsumer, GogGameWithFiles gogGame) {
        if (gogGame == null) {
            return;
        }
        List<FileSource> fileSources = gogGameWithFilesMapper.toFileSources(gogGame);
        for (FileSource fileSource : fileSources) {
            fileSourceConsumer.accept(fileSource);
        }
    }

    @Override
    public void stopFileDiscovery() {
        shouldStopFileDiscovery.set(true);
    }
}
