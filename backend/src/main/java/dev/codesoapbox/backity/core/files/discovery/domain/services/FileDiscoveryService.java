package dev.codesoapbox.backity.core.files.discovery.domain.services;

import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFile;
import dev.codesoapbox.backity.core.files.discovery.domain.model.messages.FileDiscoveryMessageTopics;
import dev.codesoapbox.backity.core.files.discovery.domain.model.messages.FileDiscoveryStatus;
import dev.codesoapbox.backity.core.files.discovery.domain.repositories.DiscoveredFileRepository;
import dev.codesoapbox.backity.core.shared.domain.services.MessageService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toList;

@Slf4j
public class FileDiscoveryService {

    private final List<SourceFileDiscoveryService> discoveryServices;
    private final DiscoveredFileRepository repository;
    private final MessageService messageService;
    private final Map<String, Boolean> discoveryStatuses = new ConcurrentHashMap<>();

    public FileDiscoveryService(List<SourceFileDiscoveryService> discoveryServices,
                                DiscoveredFileRepository repository, MessageService messageService) {
        this.discoveryServices = discoveryServices;
        this.repository = repository;
        this.messageService = messageService;

        discoveryServices.forEach(s -> {
            discoveryStatuses.put(s.getSource(), false);
            s.subscribeToProgress(System.out::println);
        });
    }

    public void discoverNewFiles() {
        log.info("Discovering new files...");

        discoveryServices.forEach(this::discoverNewFiles);
    }

    private void discoverNewFiles(SourceFileDiscoveryService discoveryService) {
        if (alreadyInProgress(discoveryService)) {
            log.info("Discovery for {} is already in progress. Aborting additional discovery.",
                    discoveryService.getSource());
            return;
        }

        log.info("Discovering files for source: {}", discoveryService.getSource());

        changeDiscoveryStatus(discoveryService, true);

        CompletableFuture.runAsync(() -> discoveryService.discoverNewFiles(this::saveDiscoveredFileInfo))
                .whenComplete((v, t) -> changeDiscoveryStatus(discoveryService, false));
    }

    private Boolean alreadyInProgress(SourceFileDiscoveryService discoveryService) {
        return discoveryStatuses.get(discoveryService.getSource());
    }

    private void changeDiscoveryStatus(SourceFileDiscoveryService discoveryService, boolean status) {
        discoveryStatuses.put(discoveryService.getSource(), status);
        sendDiscoveryStatusMessage(discoveryService, status);
    }

    private void sendDiscoveryStatusMessage(SourceFileDiscoveryService discoveryService, boolean status) {
        messageService.sendMessage(FileDiscoveryMessageTopics.FILE_DISCOVERY_STATUS.toString(),
                new FileDiscoveryStatus(discoveryService.getSource(), status));
    }

    private void saveDiscoveredFileInfo(DiscoveredFile discoveredFile) {
        if (!repository.existsById(discoveredFile.getId())) {
            repository.save(discoveredFile);
            messageService.sendMessage(FileDiscoveryMessageTopics.FILE_DISCOVERY.toString(), discoveredFile);
            log.info("Discovered new file: {} (game: {})",
                    discoveredFile.getId().getUrl(), discoveredFile.getGameTitle());
        }
    }

    public List<FileDiscoveryStatus> getStatuses() {
        return discoveryStatuses.entrySet().stream()
                .map(s -> new FileDiscoveryStatus(s.getKey(), s.getValue()))
                .collect(toList());
    }
}