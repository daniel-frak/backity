package dev.codesoapbox.backity.core.discovery.application;

import dev.codesoapbox.backity.DoNotMutate;
import dev.codesoapbox.backity.core.backup.application.downloadprogress.ProgressInfo;
import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryProgress;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryProgressRepository;
import dev.codesoapbox.backity.core.discovery.domain.events.GameFileDiscoveredEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryProgressChangedEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStatusChangedEvent;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.gamefile.domain.FileSource;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

@Slf4j
public class GameContentDiscoveryService {

    private final List<GameProviderFileDiscoveryService> gameProviderFileDiscoveryServices;
    private final GameFileRepository gameFileRepository;
    private final GameRepository gameRepository;
    private final DomainEventPublisher domainEventPublisher;
    private final GameContentDiscoveryProgressRepository discoveryProgressRepository;
    private final Map<GameProviderId, Boolean> discoveryStatuses = new ConcurrentHashMap<>();

    public GameContentDiscoveryService(List<GameProviderFileDiscoveryService> gameProviderFileDiscoveryServices,
                                       GameRepository gameRepository,
                                       GameFileRepository gameFileRepository,
                                       DomainEventPublisher domainEventPublisher,
                                       GameContentDiscoveryProgressRepository discoveryProgressRepository) {
        this.gameProviderFileDiscoveryServices = gameProviderFileDiscoveryServices.stream().toList();
        this.gameRepository = gameRepository;
        this.gameFileRepository = gameFileRepository;
        this.domainEventPublisher = domainEventPublisher;
        this.discoveryProgressRepository = discoveryProgressRepository;

        gameProviderFileDiscoveryServices.forEach(discoveryService -> {
            discoveryStatuses.put(discoveryService.getGameProviderId(), false);
            discoveryService.subscribeToProgress(progressInfo -> publishProgressChangedEvent(
                    domainEventPublisher, discoveryService.getGameProviderId(), progressInfo));
        });
    }

    private void publishProgressChangedEvent(DomainEventPublisher eventPublisher, GameProviderId gameProviderId,
                                             ProgressInfo progress) {
        int percentage = progress.percentage();
        Duration timeLeft = progress.timeLeft();
        var event = new GameContentDiscoveryProgressChangedEvent(gameProviderId, percentage, timeLeft);
        eventPublisher.publish(event);
        log.debug("Discovery progress: {}", progress);
    }

    public void startContentDiscovery() {
        log.info("Discovering content...");

        gameProviderFileDiscoveryServices.forEach(this::startContentDiscovery);
    }

    private void startContentDiscovery(GameProviderFileDiscoveryService gameProviderDiscoveryService) {
        if (alreadyInProgress(gameProviderDiscoveryService)) {
            log.info("Discovery for {} is already in progress. Aborting additional discovery.",
                    gameProviderDiscoveryService.getGameProviderId());
            return;
        }

        log.info("Discovering content for gameProviderId: {}", gameProviderDiscoveryService.getGameProviderId());

        changeDiscoveryStatus(gameProviderDiscoveryService, true);

        CompletableFuture.runAsync(() -> gameProviderDiscoveryService.discoverAllFiles(this::saveDiscoveredFileInfo))
                .whenComplete(getCompletedGameContentDiscoveryHandler().handle(gameProviderDiscoveryService));
    }

    CompletedGameContentDiscoveryHandler getCompletedGameContentDiscoveryHandler() {
        return new CompletedGameContentDiscoveryHandler();
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
        var event = new GameContentDiscoveryStatusChangedEvent(discoveryService.getGameProviderId(), isInProgress);
        domainEventPublisher.publish(event);
    }

    private void saveDiscoveredFileInfo(FileSource fileSource) {
        Game game = getGameOrAddNew(fileSource);
        GameFile gameFile = GameFile.createFor(game, fileSource);

        if (!gameFileRepository.existsByUrlAndVersion(gameFile.getFileSource().url(),
                gameFile.getFileSource().version())) {
            gameFileRepository.save(gameFile);
            domainEventPublisher.publish(GameFileDiscoveredEvent.from(gameFile));
            log.info("Discovered new file: {} (gameId: {})", gameFile.getFileSource().url(),
                    gameFile.getGameId().value());
        }
    }

    private Game getGameOrAddNew(FileSource fileSource) {
        return gameRepository.findByTitle(fileSource.originalGameTitle())
                .orElseGet(() -> addNewGame(fileSource));
    }

    private Game addNewGame(FileSource fileSource) {
        var newGame = Game.createNew(fileSource.originalGameTitle());
        gameRepository.save(newGame);
        log.info("Discovered new game: {} (id: {})", newGame.getTitle(), newGame.getId().value());

        return newGame;
    }

    public void stopContentDiscovery() {
        log.info("Stopping game content discovery...");

        gameProviderFileDiscoveryServices.forEach(this::stopContentDiscovery);
    }

    private void stopContentDiscovery(GameProviderFileDiscoveryService discoveryService) {
        if (!alreadyInProgress(discoveryService)) {
            log.info("Discovery for {} is not in progress. No need to stop.", discoveryService.getGameProviderId());
            return;
        }

        log.info("Stopping discovery for gameProviderId: {}", discoveryService.getGameProviderId());

        discoveryService.stopFileDiscovery();
    }

    public List<GameContentDiscoveryStatus> getStatuses() {
        Map<GameProviderId, GameContentDiscoveryProgress> discoveryProgressesByGameProviderId =
                getDiscoveryProgressesByGameProviderId();

        return discoveryStatuses.entrySet().stream()
                .map(s -> new GameContentDiscoveryStatus(
                        s.getKey(),
                        s.getValue(),
                        discoveryProgressesByGameProviderId.get(s.getKey())
                ))
                .toList();
    }

    private Map<GameProviderId, GameContentDiscoveryProgress> getDiscoveryProgressesByGameProviderId() {
        List<GameContentDiscoveryProgress> discoveryProgresses =
                discoveryProgressRepository.findAllByGameProviderIdIn(discoveryStatuses.keySet());

        return discoveryProgresses.stream()
                .collect(Collectors.toMap(GameContentDiscoveryProgress::gameProviderId, identity()));
    }

    class CompletedGameContentDiscoveryHandler {

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
