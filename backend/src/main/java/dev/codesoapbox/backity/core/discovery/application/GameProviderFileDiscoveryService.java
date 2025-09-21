package dev.codesoapbox.backity.core.discovery.application;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.gamefile.domain.FileSource;

import java.util.function.Consumer;

public interface GameProviderFileDiscoveryService {

    GameProviderId getGameProviderId();

    void discoverAllFiles(Consumer<FileSource> fileSourceConsumer, GameDiscoveryProgressTracker progressTracker);

    void stopFileDiscovery();
}
