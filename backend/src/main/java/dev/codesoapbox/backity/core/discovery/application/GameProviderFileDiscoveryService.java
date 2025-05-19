package dev.codesoapbox.backity.core.discovery.application;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.gamefile.domain.FileSource;
import dev.codesoapbox.backity.core.backup.application.downloadprogress.ProgressInfo;

import java.util.function.Consumer;

public interface GameProviderFileDiscoveryService {

    GameProviderId getGameProviderId();

    void discoverAllFiles(Consumer<FileSource> fileSourceConsumer);

    void stopFileDiscovery();

    void subscribeToProgress(Consumer<ProgressInfo> progressConsumer);

    ProgressInfo getProgress();
}
