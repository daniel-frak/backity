package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.embed;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.IncrementalProgressTracker;
import dev.codesoapbox.backity.core.backup.application.downloadprogress.ProgressInfo;
import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.application.GameProviderFileDiscoveryService;
import dev.codesoapbox.backity.core.gamefile.domain.FileSource;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogGameWithFiles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class GogFileDiscoveryService implements GameProviderFileDiscoveryService {

    private final GogEmbedWebClient gogEmbedWebClient;

    private final List<Consumer<ProgressInfo>> progressConsumers = new ArrayList<>();

    private final AtomicBoolean shouldStopFileDiscovery = new AtomicBoolean();
    private final GogGameWithFilesMapper gogGameWithFilesMapper;
    private IncrementalProgressTracker progressTracker;

    public GameProviderId getGameProviderId() {
        return GogGameProviderId.get();
    }

    @Override
    public void discoverAllFiles(Consumer<FileSource> fileSourceConsumer) {
        log.info("Discovering new files...");

        shouldStopFileDiscovery.set(false);
        List<String> libraryGameIds = gogEmbedWebClient.getLibraryGameIds();
        int totalGames = libraryGameIds.size();
        progressTracker = new IncrementalProgressTracker((long) totalGames, Clock.systemDefaultZone());

        libraryGameIds.stream()
                .takeWhile(id -> !shouldStopFileDiscovery.get())
                .forEach(id -> {
                    GogGameWithFiles details = gogEmbedWebClient.getGameDetails(id);
                    processFiles(fileSourceConsumer, details);
                    incrementProgress();
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
    public ProgressInfo getProgressInfo() {
        if (progressTracker == null) {
            return ProgressInfo.none();
        }
        return progressTracker.getProgressInfo();
    }
}
