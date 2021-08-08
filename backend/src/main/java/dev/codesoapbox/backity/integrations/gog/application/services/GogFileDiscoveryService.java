package dev.codesoapbox.backity.integrations.gog.application.services;

import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFile;
import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFileId;
import dev.codesoapbox.backity.core.files.discovery.domain.services.SourceFileDiscoveryService;
import dev.codesoapbox.backity.integrations.gog.application.dto.embed.GameDetailsResponse;
import dev.codesoapbox.backity.integrations.gog.application.services.embed.GogEmbedClient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class GogFileDiscoveryService implements SourceFileDiscoveryService {

    private final GogEmbedClient gogEmbedClient;

    @Getter
    private final String source = "GOG";

    @Override
    public void discoverNewFiles(Consumer<DiscoveredFile> discoveredFileConsumer) {

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
                discoveredFile.setSource("GOG");
                discoveredFile.setName(fileDetails.getName());
                discoveredFile.setGameTitle(details.getTitle());
                discoveredFile.setSize(fileDetails.getSize());

                discoveredFileConsumer.accept(discoveredFile);
            });
        });
    }
}
