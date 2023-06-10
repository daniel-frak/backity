package dev.codesoapbox.backity.core.files.domain.discovery.services;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.model.SourceFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileDetailsRepository;
import dev.codesoapbox.backity.core.files.domain.discovery.model.ProgressInfo;
import dev.codesoapbox.backity.core.files.domain.discovery.model.messages.FileDiscoveryProgress;
import dev.codesoapbox.backity.core.files.domain.discovery.model.messages.FileDiscoveryStatus;
import dev.codesoapbox.backity.core.files.domain.game.Game;
import dev.codesoapbox.backity.core.files.domain.game.GameRepository;
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
    private final GameFileDetailsRepository fileRepository;
    private final FileDiscoveryMessageService messageService;
    private final Map<String, Boolean> discoveryStatuses = new ConcurrentHashMap<>();

    public FileDiscoveryService(List<SourceFileDiscoveryService> discoveryServices,
                                GameRepository gameRepository,
                                GameFileDetailsRepository fileRepository, FileDiscoveryMessageService messageService) {
        this.discoveryServices = discoveryServices;
        this.gameRepository = gameRepository;
        this.fileRepository = fileRepository;
        this.messageService = messageService;

        discoveryServices.forEach(s -> {
            discoveryStatuses.put(s.getSource(), false);
            s.subscribeToProgress(p -> onProgressMade(messageService, s.getSource(), p));
        });
    }

    private void onProgressMade(FileDiscoveryMessageService messageService, String source, ProgressInfo progress) {
        var payload = new FileDiscoveryProgress(source, progress.percentage(), progress.timeLeft().getSeconds());
        messageService.sendProgress(payload);
        log.debug("Discovery progress: " + progress);
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
            return (v, e) -> {
                if (e != null) {
                    log.error("An exception occurred while running file discovery", e);
                }
                changeDiscoveryStatus(discoveryService, false);
            };
        }

    }

    private boolean alreadyInProgress(SourceFileDiscoveryService discoveryService) {
        return discoveryStatuses.get(discoveryService.getSource());
    }

    private void changeDiscoveryStatus(SourceFileDiscoveryService discoveryService, boolean status) {
        discoveryStatuses.put(discoveryService.getSource(), status);
        sendDiscoveryStatusMessage(discoveryService, status);
    }

    private void sendDiscoveryStatusMessage(SourceFileDiscoveryService discoveryService, boolean status) {
        messageService.sendStatus(new FileDiscoveryStatus(discoveryService.getSource(), status));
    }

    private void saveDiscoveredFileInfo(SourceFileDetails sourceFileDetails) {
        Game game = getGameOrCreateNew(sourceFileDetails);
        GameFileDetails gameFileDetails = sourceFileDetails.associateWith(game);

        if (!fileRepository.existsByUrlAndVersion(gameFileDetails.getUrl(), gameFileDetails.getVersion())) {
            fileRepository.save(gameFileDetails);
            messageService.sendDiscoveredFile(gameFileDetails);
            log.info("Discovered new file: {} (gameId: {})", gameFileDetails.getUrl(), gameFileDetails.getGameTitle());
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

    public List<FileDiscoveryStatus> getStatuses() {
        return discoveryStatuses.entrySet().stream()
                .map(s -> new FileDiscoveryStatus(s.getKey(), s.getValue()))
                .toList();
    }
}
