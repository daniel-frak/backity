package dev.codesoapbox.backity.core.files.discovery.domain.services;

import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFile;
import dev.codesoapbox.backity.core.files.discovery.domain.model.ProgressInfo;

import java.util.function.Consumer;

public interface SourceFileDiscoveryService {

    String getSource();

    void startFileDiscovery(Consumer<DiscoveredFile> discoveredFileConsumer);

    void stopFileDiscovery();

    void subscribeToProgress(Consumer<ProgressInfo> progressConsumer);

    ProgressInfo getProgress();
}
