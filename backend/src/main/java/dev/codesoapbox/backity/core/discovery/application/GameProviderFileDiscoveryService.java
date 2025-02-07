package dev.codesoapbox.backity.core.discovery.application;

import dev.codesoapbox.backity.core.gamefile.domain.GameProviderFile;
import dev.codesoapbox.backity.core.backup.application.downloadprogress.ProgressInfo;

import java.util.function.Consumer;

public interface GameProviderFileDiscoveryService {

    String getGameProviderId();

    void startFileDiscovery(Consumer<GameProviderFile> fileConsumer);

    void stopFileDiscovery();

    void subscribeToProgress(Consumer<ProgressInfo> progressConsumer);

    ProgressInfo getProgress();
}
