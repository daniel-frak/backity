package dev.codesoapbox.gogbackupservice.files.discovery.application;

import dev.codesoapbox.gogbackupservice.files.discovery.domain.DiscoveredFile;
import dev.codesoapbox.gogbackupservice.files.discovery.domain.DiscoveredFileId;
import dev.codesoapbox.gogbackupservice.files.discovery.infrastructure.repositories.DiscoveredFileSpringRepository;
import dev.codesoapbox.gogbackupservice.gog.application.dto.embed.GameDetailsResponse;
import dev.codesoapbox.gogbackupservice.gog.application.services.embed.GogEmbedClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileDiscoveryService {

    private final GogEmbedClient gogEmbedClient;
    private final DiscoveredFileSpringRepository repository;

    public void discoverNewFiles() {
        log.info("Discovering new files...");
        gogEmbedClient.getLibraryGameIds().forEach(id -> {
            GameDetailsResponse details = gogEmbedClient.getGameDetails(id);
            if (details == null || details.getFiles() == null) {
                return;
            }

            details.getFiles().forEach(fileDetails -> {
                var discoveredFile = new DiscoveredFile();
                var discoveredFileId = new DiscoveredFileId(fileDetails.getManualUrl(), fileDetails.getVersion());
                discoveredFile.setId(discoveredFileId);
                discoveredFile.setName(fileDetails.getName());
                discoveredFile.setGameTitle(details.getTitle());
                discoveredFile.setSize(fileDetails.getSize());

                if(!repository.existsById(discoveredFileId)) {
                    repository.save(discoveredFile);
                    log.info("Discovered new file: {} (game: {})", fileDetails.getManualUrl(), details.getTitle());
                }
            });
        });
    }
}
