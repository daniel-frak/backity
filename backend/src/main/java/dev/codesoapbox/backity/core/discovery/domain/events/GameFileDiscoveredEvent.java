package dev.codesoapbox.backity.core.discovery.domain.events;

import dev.codesoapbox.backity.core.gamefile.domain.FileSize;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.shared.domain.DomainEvent;

public record GameFileDiscoveredEvent(
        String originalGameTitle,
        String originalFileName,
        String fileTitle,
        FileSize size
) implements DomainEvent {

    public static GameFileDiscoveredEvent from(GameFile gameFile) {
        return new GameFileDiscoveredEvent(
                gameFile.getFileSource().originalGameTitle(),
                gameFile.getFileSource().originalFileName(),
                gameFile.getFileSource().fileTitle(),
                gameFile.getFileSource().size()
        );
    }
}
