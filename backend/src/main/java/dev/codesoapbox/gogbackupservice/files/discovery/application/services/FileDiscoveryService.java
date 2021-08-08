package dev.codesoapbox.gogbackupservice.files.discovery.application.services;

import dev.codesoapbox.gogbackupservice.files.discovery.domain.model.DiscoveredFile;
import dev.codesoapbox.gogbackupservice.files.discovery.domain.services.SourceFileDiscoveryService;
import dev.codesoapbox.gogbackupservice.files.discovery.infrastructure.repositories.DiscoveredFileSpringRepository;
import dev.codesoapbox.gogbackupservice.shared.application.MessageService;
import dev.codesoapbox.gogbackupservice.shared.application.MessageTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileDiscoveryService {

    private final List<SourceFileDiscoveryService> discoveryServices;
    private final DiscoveredFileSpringRepository repository;
    private final MessageService messageService;

    public void discoverNewFiles() {
        log.info("Discovering new files...");

        discoveryServices.forEach(this::discoverNewFiles);
    }

    private void discoverNewFiles(SourceFileDiscoveryService discoveryService) {
        log.info("Discovering files for source: {}", discoveryService.getSource());
        discoveryService.discoverNewFiles(this::saveDiscoveredFileInfo);
    }

    private void saveDiscoveredFileInfo(DiscoveredFile discoveredFile) {
        if (!repository.existsById(discoveredFile.getId())) {
            repository.save(discoveredFile);
            messageService.sendMessage(MessageTopics.FILE_DISCOVERY, discoveredFile);
            log.info("Discovered new file: {} (game: {})",
                    discoveredFile.getId().getUrl(), discoveredFile.getGameTitle());
        }
    }
}
