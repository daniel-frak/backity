package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.application.GameDiscoveryProgressTracker;
import dev.codesoapbox.backity.core.discovery.application.GameProviderFileDiscoveryService;
import dev.codesoapbox.backity.core.discovery.domain.DiscoveredFile;
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
            Consumer<DiscoveredFile> discoveredFileConsumer, GameDiscoveryProgressTracker progressTracker) {
        log.info("Discovering new files...");

        shouldStopFileDiscovery.set(false);
        List<String> libraryGameIds = gogEmbedWebClient.getLibraryGameIds();
        int totalGames = libraryGameIds.size();
        progressTracker.initializeGamesDiscovered(totalGames);

        libraryGameIds.stream()
                .takeWhile(_ -> !shouldStopFileDiscovery.get())
                .forEach(id -> {
                    GogGameWithFiles details = gogEmbedWebClient.getGameDetails(id);
                    processFiles(discoveredFileConsumer, details);
                    progressTracker.incrementGamesDiscovered(1);
                });
    }

    private void processFiles(Consumer<DiscoveredFile> fileSourceConsumer, GogGameWithFiles gogGame) {
        if (gogGame == null) {
            return;
        }
        List<DiscoveredFile> discoveredFiles = gogGameWithFilesMapper.toDiscoveredFiles(gogGame);
        for (DiscoveredFile discoveredFile : discoveredFiles) {
            fileSourceConsumer.accept(discoveredFile);
        }
    }

    @Override
    public void stopFileDiscovery() {
        shouldStopFileDiscovery.set(true);
    }
}
