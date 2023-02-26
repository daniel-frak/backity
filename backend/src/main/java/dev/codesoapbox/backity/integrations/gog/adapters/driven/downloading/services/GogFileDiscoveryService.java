package dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.services;

import dev.codesoapbox.backity.core.files.domain.discovery.model.IncrementalProgressTracker;
import dev.codesoapbox.backity.core.files.domain.discovery.model.ProgressInfo;
import dev.codesoapbox.backity.core.files.domain.discovery.services.SourceFileDiscoveryService;
import dev.codesoapbox.backity.core.files.domain.downloading.model.DownloadStatus;
import dev.codesoapbox.backity.core.files.domain.downloading.model.GameFileVersion;
import dev.codesoapbox.backity.integrations.gog.domain.model.embed.GameDetailsResponse;
import dev.codesoapbox.backity.integrations.gog.domain.model.embed.GameFileDetailsResponse;
import dev.codesoapbox.backity.integrations.gog.domain.services.GogEmbedClient;
import lombok.Getter;
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

    @Getter
    private final String source = "GOG";

    private final List<Consumer<ProgressInfo>> progressConsumers = new ArrayList<>();

    private final AtomicBoolean shouldStopFileDiscovery = new AtomicBoolean();
    private IncrementalProgressTracker progressTracker;

    @Override
    public void startFileDiscovery(Consumer<GameFileVersion> gameFileConsumer) {
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

    private void processFiles(Consumer<GameFileVersion> discoveredFileConsumer, GameDetailsResponse details) {
        if (details == null || details.getFiles() == null) {
            return;
        }
        details.getFiles().forEach(fileDetails -> {
            GameFileVersion gameFileVersion = mapToGameFileVersion(details, fileDetails);
            discoveredFileConsumer.accept(gameFileVersion);
        });
    }

    private GameFileVersion mapToGameFileVersion(GameDetailsResponse details, GameFileDetailsResponse fileDetails) {
        var gameFile = new GameFileVersion();
        gameFile.setUrl(fileDetails.getManualUrl());
        gameFile.setVersion(fileDetails.getVersion());
        gameFile.setSource("GOG");
        gameFile.setName(fileDetails.getName());
        gameFile.setGameTitle(details.getTitle());
        gameFile.setSize(fileDetails.getSize());
        gameFile.setStatus(DownloadStatus.DISCOVERED);
        return gameFile;
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
