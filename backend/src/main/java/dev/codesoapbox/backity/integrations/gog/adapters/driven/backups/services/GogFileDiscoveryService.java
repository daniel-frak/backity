package dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services;

import dev.codesoapbox.backity.core.discovery.domain.IncrementalProgressTracker;
import dev.codesoapbox.backity.core.discovery.domain.ProgressInfo;
import dev.codesoapbox.backity.core.discovery.domain.SourceFileDiscoveryService;
import dev.codesoapbox.backity.core.gamefiledetails.domain.SourceFileDetails;
import dev.codesoapbox.backity.integrations.gog.domain.model.embed.GameDetailsResponse;
import dev.codesoapbox.backity.integrations.gog.domain.model.embed.GameFileDetailsResponse;
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
public class GogFileDiscoveryService implements SourceFileDiscoveryService {

    private final GogEmbedClient gogEmbedClient;

    private final List<Consumer<ProgressInfo>> progressConsumers = new ArrayList<>();

    private final AtomicBoolean shouldStopFileDiscovery = new AtomicBoolean();
    private IncrementalProgressTracker progressTracker;

    public String getSource() {
        return "GOG";
    }

    @Override
    public void startFileDiscovery(Consumer<SourceFileDetails> gameFileConsumer) {
        log.info("Discovering new files...");

        shouldStopFileDiscovery.set(false);
        List<String> libraryGameIds = gogEmbedClient.getLibraryGameIds();
        int totalGames = libraryGameIds.size();
        progressTracker = new IncrementalProgressTracker((long) totalGames, Clock.systemDefaultZone());

        libraryGameIds.stream()
                .takeWhile(id -> !shouldStopFileDiscovery.get())
                .forEach(id -> {
                    GameDetailsResponse details = gogEmbedClient.getGameDetails(id);
                    processFiles(gameFileConsumer, details);
                    incrementProgress();
                });
    }

    private void processFiles(Consumer<SourceFileDetails> discoveredFileConsumer, GameDetailsResponse details) {
        if (details == null || details.getFiles() == null) {
            return;
        }
        details.getFiles().forEach(fileDetails -> {
            SourceFileDetails sourceFileDetails = mapToDiscoveredGameFile(details, fileDetails);
            discoveredFileConsumer.accept(sourceFileDetails);
        });
    }

    private SourceFileDetails mapToDiscoveredGameFile(GameDetailsResponse gameDetails,
                                                      GameFileDetailsResponse fileDetails) {
        return new SourceFileDetails(
                "GOG",
                gameDetails.getTitle(),
                fileDetails.getName(),
                fileDetails.getVersion(),
                fileDetails.getManualUrl(),
                fileDetails.getFileTitle(),
                fileDetails.getSize()
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
