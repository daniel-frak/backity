package dev.codesoapbox.backity.core.discovery.application;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.domain.DiscoveredFile;

import java.util.function.Consumer;

public interface GameProviderFileDiscoveryService {

    GameProviderId getGameProviderId();

    void discoverAllFiles(Consumer<DiscoveredFile> discoveredFileConsumer,
                          GameDiscoveryProgressTracker progressTracker);

    void stopFileDiscovery();
}
