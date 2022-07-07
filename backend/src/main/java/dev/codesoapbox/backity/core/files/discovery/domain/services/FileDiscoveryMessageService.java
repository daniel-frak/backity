package dev.codesoapbox.backity.core.files.discovery.domain.services;

import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFile;
import dev.codesoapbox.backity.core.files.discovery.domain.model.messages.FileDiscoveryProgress;
import dev.codesoapbox.backity.core.files.discovery.domain.model.messages.FileDiscoveryStatus;

public interface FileDiscoveryMessageService {

    void sendStatus(FileDiscoveryStatus payload);
    void sendProgress(FileDiscoveryProgress payload);
    void sendDiscoveredFile(DiscoveredFile payload);
}
