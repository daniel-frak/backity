package dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.gamefile.domain.FileSize;
import dev.codesoapbox.backity.shared.domain.IncrementalProgressTracker;
import dev.codesoapbox.backity.shared.domain.ProgressInfo;
import dev.codesoapbox.backity.core.discovery.application.GameProviderFileDiscoveryService;
import dev.codesoapbox.backity.core.gamefile.domain.GameProviderFile;
import dev.codesoapbox.backity.integrations.gog.domain.model.embed.GameFileResponse;
import dev.codesoapbox.backity.integrations.gog.domain.model.embed.GameDetailsResponse;
import dev.codesoapbox.backity.integrations.gog.domain.services.GogEmbedClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class GogFileDiscoveryServiceGame implements GameProviderFileDiscoveryService {

    private static final GameProviderId GAME_PROVIDER_ID = new GameProviderId("GOG");

    private final GogEmbedClient gogEmbedClient;

    private final List<Consumer<ProgressInfo>> progressConsumers = new ArrayList<>();

    private final AtomicBoolean shouldStopFileDiscovery = new AtomicBoolean();
    private IncrementalProgressTracker progressTracker;

    public String getGameProviderId() {
        return "GOG";
    }

    @Override
    public void startFileDiscovery(Consumer<GameProviderFile> fileConsumer) {
        log.info("Discovering new files...");

        shouldStopFileDiscovery.set(false);
        List<String> libraryGameIds = gogEmbedClient.getLibraryGameIds();
        int totalGames = libraryGameIds.size();
        progressTracker = new IncrementalProgressTracker((long) totalGames, Clock.systemDefaultZone());

        libraryGameIds.stream()
                .takeWhile(id -> !shouldStopFileDiscovery.get())
                .forEach(id -> {
                    GameDetailsResponse details = gogEmbedClient.getGameDetails(id);
                    processFiles(fileConsumer, details);
                    incrementProgress();
                });
    }

    private void processFiles(Consumer<GameProviderFile> discoveredFileConsumer, GameDetailsResponse details) {
        if (details == null || details.getFiles() == null) {
            return;
        }
        details.getFiles().forEach(gameFile -> {
            GameProviderFile gameProviderFile = mapToDiscoveredFile(details, gameFile);
            discoveredFileConsumer.accept(gameProviderFile);
        });
    }

    private GameProviderFile mapToDiscoveredFile(GameDetailsResponse gameDetails,
                                                 GameFileResponse gameFile) {
        return new GameProviderFile(
                GAME_PROVIDER_ID,
                gameDetails.getTitle(),
                gameFile.getName(),
                gameFile.getVersion(),
                gameFile.getManualUrl(),
                gameFile.getFileTitle(),
                FileSize.fromString(gameFile.getSize())
        );
    }

    private void incrementProgress() {
        progressTracker.increment();
        updateProgress(progressTracker.getProgressInfo());
    }

    @Override
    public void stopFileDiscovery() {
        shouldStopFileDiscovery.set(true);
    }

    private void updateProgress(ProgressInfo progressInfo) {
        progressConsumers.forEach(c -> c.accept(progressInfo));
    }

    @Override
    public void subscribeToProgress(Consumer<ProgressInfo> progressConsumer) {
        progressConsumers.add(progressConsumer);
    }

    @Override
    public ProgressInfo getProgress() {
        if (progressTracker == null) {
            return ProgressInfo.none();
        }
        return progressTracker.getProgressInfo();
    }
}
