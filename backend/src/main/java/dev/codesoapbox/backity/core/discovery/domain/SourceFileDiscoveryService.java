package dev.codesoapbox.backity.core.discovery.domain;

import dev.codesoapbox.backity.core.filedetails.domain.SourceFileDetails;

import java.util.function.Consumer;

public interface SourceFileDiscoveryService {

    String getSource();

    void startFileDiscovery(Consumer<SourceFileDetails> fileConsumer);

    void stopFileDiscovery();

    void subscribeToProgress(Consumer<ProgressInfo> progressConsumer);

    ProgressInfo getProgress();
}
