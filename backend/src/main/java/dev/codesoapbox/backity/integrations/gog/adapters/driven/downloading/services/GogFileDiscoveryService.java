package dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.services;

import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFile;
import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFileId;
import dev.codesoapbox.backity.core.files.discovery.domain.model.IncrementalProgressTracker;
import dev.codesoapbox.backity.core.files.discovery.domain.model.ProgressInfo;
import dev.codesoapbox.backity.core.files.discovery.domain.services.SourceFileDiscoveryService;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.model.embed.GameDetailsResponse;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.services.embed.GogEmbedClient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class GogFileDiscoveryService implements SourceFileDiscoveryService {

    private final GogEmbedClient gogEmbedClient;

    @Getter
    private final String source = "GOG";

    private final List<Consumer<ProgressInfo>> progressConsumers = new ArrayList<>();
    private IncrementalProgressTracker progressTracker;

    @Override
    public void discoverNewFiles(Consumer<DiscoveredFile> discoveredFileConsumer) {
        log.info("Discovering new files...");

        List<String> libraryGameIds = gogEmbedClient.getLibraryGameIds();
        int totalGames = libraryGameIds.size();
        progressTracker = new IncrementalProgressTracker((long) totalGames);

        libraryGameIds.forEach(id -> {
            GameDetailsResponse details = gogEmbedClient.getGameDetails(id);
            if (details == null || details.getFiles() == null) {
                return;
            }

            details.getFiles().forEach(fileDetails -> {
                var discoveredFile = new DiscoveredFile();
                var discoveredFileId = new DiscoveredFileId(fileDetails.getManualUrl(), fileDetails.getVersion());
                discoveredFile.setId(discoveredFileId);
                discoveredFile.setSource("GOG");
                discoveredFile.setName(fileDetails.getName());
                discoveredFile.setGameTitle(details.getTitle());
                discoveredFile.setSize(fileDetails.getSize());

                discoveredFileConsumer.accept(discoveredFile);
            });

            progressTracker.increment();
            updateProgress(progressTracker.getProgressInfo());
        });
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
        if(progressTracker == null) {
            return ProgressInfo.none();
        }
        return progressTracker.getProgressInfo();
    }
}
