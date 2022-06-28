package dev.codesoapbox.backity.integrations.gog.domain.services;

import dev.codesoapbox.backity.integrations.gog.domain.model.embed.GameDetailsResponse;

import java.util.List;

public interface GogEmbedClient {
    String getLibrarySize();

    List<String> getLibraryGameIds();

    GameDetailsResponse getGameDetails(String gameId);
}
