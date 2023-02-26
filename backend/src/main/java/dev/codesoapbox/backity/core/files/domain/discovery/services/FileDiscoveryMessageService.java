package dev.codesoapbox.backity.core.files.domain.discovery.services;

import dev.codesoapbox.backity.core.files.domain.discovery.model.messages.FileDiscoveryProgress;
import dev.codesoapbox.backity.core.files.domain.discovery.model.messages.FileDiscoveryStatus;
import dev.codesoapbox.backity.core.files.domain.downloading.model.GameFileVersion;

public interface FileDiscoveryMessageService {

    void sendStatus(FileDiscoveryStatus payload);

    void sendProgress(FileDiscoveryProgress payload);

    void sendDiscoveredFile(GameFileVersion payload);
}
