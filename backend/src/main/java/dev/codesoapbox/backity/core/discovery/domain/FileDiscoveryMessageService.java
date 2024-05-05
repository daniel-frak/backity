package dev.codesoapbox.backity.core.discovery.domain;

import dev.codesoapbox.backity.core.discovery.domain.messages.FileDiscoveryProgress;
import dev.codesoapbox.backity.core.discovery.domain.messages.FileDiscoveryStatus;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;

public interface FileDiscoveryMessageService {

    void sendStatusChangedMessage(FileDiscoveryStatus status);

    void sendProgressUpdateMessage(FileDiscoveryProgress progress);

    void sendFileDiscoveredMessage(FileDetails payload);
}
