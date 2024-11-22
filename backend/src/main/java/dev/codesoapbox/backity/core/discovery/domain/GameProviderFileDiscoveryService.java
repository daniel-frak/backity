package dev.codesoapbox.backity.core.discovery.domain;

import dev.codesoapbox.backity.core.gamefile.domain.GameProviderFile;

import java.util.function.Consumer;

public interface GameProviderFileDiscoveryService {

    String getGameProviderId();

    void startFileDiscovery(Consumer<GameProviderFile> fileConsumer);

    void stopFileDiscovery();

    void subscribeToProgress(Consumer<ProgressInfo> progressConsumer);

    ProgressInfo getProgress();
}
