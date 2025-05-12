package dev.codesoapbox.backity.gameproviders.gog.domain;

import java.util.List;

public interface GogLibraryService {

    String getLibrarySize();

    List<String> getLibraryGameIds();

    GogGameWithFiles getGameDetails(String gameId);
}
