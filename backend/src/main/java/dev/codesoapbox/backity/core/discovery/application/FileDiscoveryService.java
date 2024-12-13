package dev.codesoapbox.backity.core.discovery.application;

import dev.codesoapbox.backity.DoNotMutate;
import dev.codesoapbox.backity.shared.domain.ProgressInfo;
import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveredEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveryProgressChangedEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveryStatusChangedEvent;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.gamefile.domain.GameProviderFile;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

@Slf4j
public class FileDiscoveryService {

    private final List<GameProviderFileDiscoveryService> discoveryServices;
    private final GameRepository gameRepository;
    private final GameFileRepository fileRepository;
    private final DomainEventPublisher domainEventPublisher;
    private final Map<String, Boolean> discoveryStatuses = new ConcurrentHashMap<>();

    public FileDiscoveryService(List<GameProviderFileDiscoveryService> discoveryServices,
                                GameRepository gameRepository,
                                GameFileRepository fileRepository,
                                DomainEventPublisher domainEventPublisher) {
        this.discoveryServices = discoveryServices.stream().toList();
        this.gameRepository = gameRepository;
        this.fileRepository = fileRepository;
        this.domainEventPublisher = domainEventPublisher;

        discoveryServices.forEach(discoveryService -> {
            discoveryStatuses.put(discoveryService.getGameProviderId(), false);
            discoveryService.subscribeToProgress(progressInfo -> publishProgressChangedEvent(
                    domainEventPublisher, discoveryService.getGameProviderId(), progressInfo));
        });
    }

    private void publishProgressChangedEvent(DomainEventPublisher eventPublisher, String gameProviderId,
                                             ProgressInfo progress) {
        int percentage = progress.percentage();
        long seconds = progress.timeLeft().getSeconds();
        var event = new FileDiscoveryProgressChangedEvent(gameProviderId, percentage, seconds);
        eventPublisher.publish(event);
        log.debug("Discovery progress: {}", progress);
    }

    public void startFileDiscovery() {
        log.info("Discovering new files...");

        discoveryServices.forEach(this::startFileDiscovery);
    }

    private void startFileDiscovery(GameProviderFileDiscoveryService discoveryService) {
        if (alreadyInProgress(discoveryService)) {
            log.info("Discovery for {} is already in progress. Aborting additional discovery.",
                    discoveryService.getGameProviderId());
            return;
        }

        log.info("Discovering files for gameProviderId: {}", discoveryService.getGameProviderId());

        changeDiscoveryStatus(discoveryService, true);

        CompletableFuture.runAsync(() -> discoveryService.startFileDiscovery(this::saveDiscoveredFileInfo))
                .whenComplete(getCompletedFileDiscoveryHandler().handle(discoveryService));
    }

    CompletedFileDiscoveryHandler getCompletedFileDiscoveryHandler() {
        return new CompletedFileDiscoveryHandler();
    }

    private boolean alreadyInProgress(GameProviderFileDiscoveryService discoveryService) {
        return discoveryStatuses.get(discoveryService.getGameProviderId());
    }

    private void changeDiscoveryStatus(GameProviderFileDiscoveryService discoveryService, boolean isInProgress) {
        log.info("Changing discovery status of {} to {}", discoveryService.getGameProviderId(), isInProgress);
        discoveryStatuses.put(discoveryService.getGameProviderId(), isInProgress);
        sendDiscoveryStatusChangedEvent(discoveryService, isInProgress);
    }

    private void sendDiscoveryStatusChangedEvent(GameProviderFileDiscoveryService discoveryService,
                                                 boolean isInProgress) {
        var event = new FileDiscoveryStatusChangedEvent(discoveryService.getGameProviderId(), isInProgress);
        domainEventPublisher.publish(event);
    }

    private void saveDiscoveredFileInfo(GameProviderFile gameProviderFile) {
        Game game = getGameOrCreateNew(gameProviderFile);
        GameFile gameFile = gameProviderFile.associateWith(game);

        if (!fileRepository.existsByUrlAndVersion(gameFile.getGameProviderFile().url(),
                gameFile.getGameProviderFile().version())) {
            fileRepository.save(gameFile);
            domainEventPublisher.publish(FileDiscoveredEvent.from(gameFile));
            log.info("Discovered new file: {} (gameId: {})", gameFile.getGameProviderFile().url(),
                    gameFile.getGameId().value());
        }
    }

    private Game getGameOrCreateNew(GameProviderFile gameProviderFile) {
        return gameRepository.findByTitle(gameProviderFile.originalGameTitle())
                .orElseGet(() -> {
                    var newGame = Game.createNew(gameProviderFile.originalGameTitle());
                    gameRepository.save(newGame);
                    return newGame;
                });
    }

    public void stopFileDiscovery() {
        log.info("Stopping file discovery...");

        discoveryServices.forEach(this::stopFileDiscovery);
    }

    private void stopFileDiscovery(GameProviderFileDiscoveryService discoveryService) {
        if (!alreadyInProgress(discoveryService)) {
            log.info("Discovery for {} is not in progress. No need to stop.", discoveryService.getGameProviderId());
            return;
        }

        log.info("Stopping discovery for gameProviderId: {}", discoveryService.getGameProviderId());

        discoveryService.stopFileDiscovery();
    }

    public List<FileDiscoveryStatus> getStatuses() {
        return discoveryStatuses.entrySet().stream()
                .map(s -> new FileDiscoveryStatus(s.getKey(), s.getValue()))
                .toList();
    }

    class CompletedFileDiscoveryHandler {

        BiConsumer<Void, Throwable> handle(GameProviderFileDiscoveryService discoveryService) {
            return (v, e) -> handle(discoveryService, e);
        }

        @DoNotMutate // Due to logging logic (difficult to test)
        private void handle(GameProviderFileDiscoveryService discoveryService, Throwable e) {
            if (e != null) {
                log.error("An exception occurred while running file discovery", e);
            }
            changeDiscoveryStatus(discoveryService, false);
        }
    }
}
