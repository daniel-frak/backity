package dev.codesoapbox.backity.integrations.gog.application;

import dev.codesoapbox.backity.integrations.gog.domain.model.embed.GameDetailsResponse;
import dev.codesoapbox.backity.integrations.gog.domain.services.GogEmbedClient;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetGogGameDetailsUseCase {

    private final GogEmbedClient gogEmbedClient;

    public GameDetailsResponse getGameDetails(String gameId) {
        return gogEmbedClient.getGameDetails(gameId);
    }
}
