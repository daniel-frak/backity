package dev.codesoapbox.backity.core.discovery.application;

import dev.codesoapbox.backity.DoNotMutate;
import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.gamefile.domain.FileSource;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
public class GameContentDiscoveryService {

    private final List<GameProviderFileDiscoveryService> gameProviderFileDiscoveryServices;
    private final GameFileRepository gameFileRepository;
    private final GameRepository gameRepository;
    private final GameContentDiscoveryProgressTracker discoveryProgressTracker;
    private final ExecutorService discoveryExecutor;

    public GameContentDiscoveryService(List<GameProviderFileDiscoveryService> gameProviderFileDiscoveryServices,
                                       GameRepository gameRepository,
                                       GameFileRepository gameFileRepository,
                                       GameContentDiscoveryProgressTracker discoveryProgressTracker,
                                       ExecutorService discoveryExecutor) {
        this.gameProviderFileDiscoveryServices = gameProviderFileDiscoveryServices.stream().toList();
        this.gameRepository = gameRepository;
        this.gameFileRepository = gameFileRepository;
        this.discoveryProgressTracker = discoveryProgressTracker;
        this.discoveryExecutor = discoveryExecutor;
    }

    public void startContentDiscovery() {
        log.info("Discovering content...");

        gameProviderFileDiscoveryServices.forEach(this::startContentDiscovery);
    }

    private void startContentDiscovery(GameProviderFileDiscoveryService gameProviderDiscoveryService) {
        if (discoveryProgressTracker.isInProgress(gameProviderDiscoveryService)) {
            log.info("Discovery for {} is already in progress. Aborting additional discovery.",
                    gameProviderDiscoveryService.getGameProviderId());
            return;
        }

        log.info("Discovering content for gameProviderId: {}", gameProviderDiscoveryService.getGameProviderId());

        discoveryProgressTracker.initializeTracking(gameProviderDiscoveryService.getGameProviderId());
        GameDiscoveryProgressTracker gameDiscoveryProgressTracker =
                discoveryProgressTracker.getGameDiscoveryTracker(
                        gameProviderDiscoveryService.getGameProviderId());
        CompletableFuture.runAsync(() -> gameProviderDiscoveryService.discoverAllFiles(
                                fileSource -> saveDiscoveredFileInfo(
                                        gameProviderDiscoveryService.getGameProviderId(), fileSource),
                                gameDiscoveryProgressTracker),
                        discoveryExecutor)
                .whenComplete((v, e) ->
                        handleGameProviderDiscoveryFinished(gameProviderDiscoveryService, e));
    }

    @DoNotMutate // Due to logging logic (difficult to test)
    private void handleGameProviderDiscoveryFinished(
            GameProviderFileDiscoveryService discoveryService, Throwable exception) {
        if (exception == null) {
            discoveryProgressTracker.markSuccessful(discoveryService.getGameProviderId());
        } else {
            log.error("An exception occurred while running file discovery", exception);
        }
        discoveryProgressTracker.finalizeTracking(discoveryService.getGameProviderId());
    }

    private void saveDiscoveredFileInfo(GameProviderId gameProviderId, FileSource fileSource) {
        Game game = getGameOrAddNew(gameProviderId, fileSource);
        GameFile gameFile = GameFile.createFor(game, fileSource);

        if (!gameFileRepository.existsByUrlAndVersion(gameFile.getFileSource().url(),
                gameFile.getFileSource().version())) {
            gameFileRepository.save(gameFile);
            discoveryProgressTracker.incrementGameFilesDiscovered(gameProviderId, 1);
            log.info("Discovered new file: {} (gameId: {})", gameFile.getFileSource().url(),
                    gameFile.getGameId().value());
        }
    }

    private Game getGameOrAddNew(GameProviderId gameProviderId, FileSource fileSource) {
        return gameRepository.findByTitle(fileSource.originalGameTitle())
                .orElseGet(() -> addNewGame(gameProviderId, fileSource));
    }

    private Game addNewGame(GameProviderId gameProviderId, FileSource fileSource) {
        Game newGame = Game.createNew(fileSource.originalGameTitle());
        gameRepository.save(newGame);
        discoveryProgressTracker.incrementGamesDiscovered(gameProviderId, 1);
        log.info("Discovered new game: {} (id: {})", newGame.getTitle(), newGame.getId().value());

        return newGame;
    }

    public void stopContentDiscovery() {
        log.info("Stopping game content discovery...");

        gameProviderFileDiscoveryServices.forEach(this::stopContentDiscovery);
    }

    private void stopContentDiscovery(GameProviderFileDiscoveryService discoveryService) {
        if (!discoveryProgressTracker.isInProgress(discoveryService)) {
            log.info("Discovery for {} is not in progress. No need to stop.", discoveryService.getGameProviderId());
            return;
        }

        log.info("Stopping discovery for gameProviderId: {}", discoveryService.getGameProviderId());

        discoveryService.stopFileDiscovery();
    }
}
