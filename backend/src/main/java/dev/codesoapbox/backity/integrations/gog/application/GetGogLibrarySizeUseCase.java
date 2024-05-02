package dev.codesoapbox.backity.integrations.gog.application;

import dev.codesoapbox.backity.integrations.gog.domain.services.GogEmbedClient;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetGogLibrarySizeUseCase {

    private final GogEmbedClient gogEmbedClient;

    public String getLibrarySize() {
        return gogEmbedClient.getLibrarySize();
    }
}
