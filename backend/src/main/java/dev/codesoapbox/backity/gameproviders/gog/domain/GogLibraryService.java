package dev.codesoapbox.backity.gameproviders.gog.domain;

public interface GogLibraryService {

    String getLibrarySize();

    GogGameWithFiles getGameDetails(String gameId);
}
