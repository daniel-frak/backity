package dev.codesoapbox.backity.core.files.domain.discovery.services;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersionBackup;
import dev.codesoapbox.backity.core.files.domain.discovery.model.messages.FileDiscoveryProgress;
import dev.codesoapbox.backity.core.files.domain.discovery.model.messages.FileDiscoveryStatus;

public interface FileDiscoveryMessageService {

    void sendStatus(FileDiscoveryStatus payload);

    void sendProgress(FileDiscoveryProgress payload);

    void sendDiscoveredFile(GameFileVersionBackup payload);
}
