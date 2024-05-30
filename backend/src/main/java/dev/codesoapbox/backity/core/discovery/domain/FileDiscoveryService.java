package dev.codesoapbox.backity.core.discovery.domain;

import dev.codesoapbox.backity.DoNotMutate;
import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveryProgressChangedEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveryStatusChangedEvent;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetailsRepository;
import dev.codesoapbox.backity.core.filedetails.domain.SourceFileDetails;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

@Slf4j
public class FileDiscoveryService {

    private final List<SourceFileDiscoveryService> discoveryServices;
    private final GameRepository gameRepository;
    private final FileDetailsRepository fileRepository;
    private final FileDiscoveryEventPublisher fileDiscoveryEventPublisher;
    private final Map<String, Boolean> discoveryStatuses = new ConcurrentHashMap<>();

    public FileDiscoveryService(List<SourceFileDiscoveryService> discoveryServices,
                                GameRepository gameRepository,
                                FileDetailsRepository fileRepository,
                                FileDiscoveryEventPublisher fileDiscoveryEventPublisher) {
        this.discoveryServices = discoveryServices.stream().toList();
        this.gameRepository = gameRepository;
        this.fileRepository = fileRepository;
        this.fileDiscoveryEventPublisher = fileDiscoveryEventPublisher;

        discoveryServices.forEach(s -> {
            discoveryStatuses.put(s.getSource(), false);
            s.subscribeToProgress(p -> onProgressMade(fileDiscoveryEventPublisher, s.getSource(), p));
        });
    }

    private void onProgressMade(FileDiscoveryEventPublisher eventPublisher, String source, ProgressInfo progress) {
        int percentage = progress.percentage();
        long seconds = progress.timeLeft().getSeconds();
        var payload = new FileDiscoveryProgressChangedEvent(source, percentage, seconds);
        eventPublisher.publishProgressChangedEvent(payload);
        log.debug("Discovery progress: {}", progress);
    }

    public void startFileDiscovery() {
        log.info("Discovering new files...");

        discoveryServices.forEach(this::startFileDiscovery);
    }

    private void startFileDiscovery(SourceFileDiscoveryService discoveryService) {
        if (alreadyInProgress(discoveryService)) {
            log.info("Discovery for {} is already in progress. Aborting additional discovery.",
                    discoveryService.getSource());
            return;
        }

        log.info("Discovering files for sourceId: {}", discoveryService.getSource());

        changeDiscoveryStatus(discoveryService, true);

        CompletableFuture.runAsync(() -> discoveryService.startFileDiscovery(this::saveDiscoveredFileInfo))
                .whenComplete(getCompletedFileDiscoveryHandler().handle(discoveryService));
    }

    CompletedFileDiscoveryHandler getCompletedFileDiscoveryHandler() {
        return new CompletedFileDiscoveryHandler();
    }

    class CompletedFileDiscoveryHandler {

        BiConsumer<Void, Throwable> handle(SourceFileDiscoveryService discoveryService) {
            return (v, e) -> handle(discoveryService, e);
        }

        @DoNotMutate // Due to logging logic (difficult to test)
        private void handle(SourceFileDiscoveryService discoveryService, Throwable e) {
            if (e != null) {
                log.error("An exception occurred while running file discovery", e);
            }
            changeDiscoveryStatus(discoveryService, false);
        }
    }

    private boolean alreadyInProgress(SourceFileDiscoveryService discoveryService) {
        return discoveryStatuses.get(discoveryService.getSource());
    }

    private void changeDiscoveryStatus(SourceFileDiscoveryService discoveryService, boolean isInProgress) {
        log.info("Changing discovery status of {} to {}" , discoveryService.getSource(), isInProgress);
        discoveryStatuses.put(discoveryService.getSource(), isInProgress);
        sendDiscoveryStatusChangedEvent(discoveryService, isInProgress);
    }

    private void sendDiscoveryStatusChangedEvent(SourceFileDiscoveryService discoveryService, boolean isInProgress) {
        var status = new FileDiscoveryStatusChangedEvent(discoveryService.getSource(), isInProgress);
        fileDiscoveryEventPublisher.publishStatusChangedEvent(status);
    }

    private void saveDiscoveredFileInfo(SourceFileDetails sourceFileDetails) {
        Game game = getGameOrCreateNew(sourceFileDetails);
        FileDetails fileDetails = sourceFileDetails.associateWith(game);

        if (!fileRepository.existsByUrlAndVersion(fileDetails.getSourceFileDetails().url(),
                fileDetails.getSourceFileDetails().version())) {
            fileRepository.save(fileDetails);
            fileDiscoveryEventPublisher.publishFileDiscoveredEvent(fileDetails);
            log.info("Discovered new file: {} (gameId: {})", fileDetails.getSourceFileDetails().url(),
                    fileDetails.getGameId().value());
        }
    }

    private Game getGameOrCreateNew(SourceFileDetails sourceFileDetails) {
        return gameRepository.findByTitle(sourceFileDetails.originalGameTitle())
                .orElseGet(() -> {
                    var newGame = Game.createNew(sourceFileDetails.originalGameTitle());
                    gameRepository.save(newGame);
                    return newGame;
                });
    }

    public void stopFileDiscovery() {
        log.info("Stopping file discovery...");

        discoveryServices.forEach(this::stopFileDiscovery);
    }

    private void stopFileDiscovery(SourceFileDiscoveryService discoveryService) {
        if (!alreadyInProgress(discoveryService)) {
            log.info("Discovery for {} is not in progress. No need to stop.", discoveryService.getSource());
            return;
        }

        log.info("Stopping discovery for sourceId: {}", discoveryService.getSource());

        discoveryService.stopFileDiscovery();
    }

    public List<FileDiscoveryStatusChangedEvent> getStatuses() {
        return discoveryStatuses.entrySet().stream()
                .map(s -> new FileDiscoveryStatusChangedEvent(s.getKey(), s.getValue()))
                .toList();
    }
}
