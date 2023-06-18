package dev.codesoapbox.backity.core.discovery.domain;

import dev.codesoapbox.backity.core.discovery.domain.messages.FileDiscoveryProgress;
import dev.codesoapbox.backity.core.discovery.domain.messages.FileDiscoveryStatus;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetails;

public interface FileDiscoveryMessageService {

    void sendStatusChangedMessage(FileDiscoveryStatus status);

    void sendProgressUpdateMessage(FileDiscoveryProgress progress);

    void sendFileDiscoveredMessage(GameFileDetails payload);
}
