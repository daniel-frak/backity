package dev.codesoapbox.backity.core.files.domain.discovery.services;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersionBackup;
import dev.codesoapbox.backity.core.files.domain.discovery.model.ProgressInfo;

import java.util.function.Consumer;

public interface SourceFileDiscoveryService {

    String getSource();

    void startFileDiscovery(Consumer<GameFileVersionBackup> gameFileConsumer);

    void stopFileDiscovery();

    void subscribeToProgress(Consumer<ProgressInfo> progressConsumer);

    ProgressInfo getProgress();
}
