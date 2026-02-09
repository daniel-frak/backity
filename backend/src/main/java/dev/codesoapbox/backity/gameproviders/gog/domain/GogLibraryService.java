package dev.codesoapbox.backity.gameproviders.gog.domain;

import java.util.Optional;

public interface GogLibraryService {

    String getLibrarySize();

    Optional<GogGameWithFiles> getGameDetails(String gameId);
}
