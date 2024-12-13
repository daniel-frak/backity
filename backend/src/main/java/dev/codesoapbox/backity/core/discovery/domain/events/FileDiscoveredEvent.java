package dev.codesoapbox.backity.core.discovery.domain.events;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.shared.domain.DomainEvent;

public record FileDiscoveredEvent(
        String originalGameTitle,
        String originalFileName,
        String fileTitle,
        String size
) implements DomainEvent {

    public static FileDiscoveredEvent from(GameFile gameFile) {
        return new FileDiscoveredEvent(
                gameFile.getGameProviderFile().originalGameTitle(),
                gameFile.getGameProviderFile().originalFileName(),
                gameFile.getGameProviderFile().fileTitle(),
                gameFile.getGameProviderFile().size()
        );
    }
}
