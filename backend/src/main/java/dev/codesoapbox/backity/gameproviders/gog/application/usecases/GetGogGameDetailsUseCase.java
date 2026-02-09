package dev.codesoapbox.backity.gameproviders.gog.application.usecases;

import dev.codesoapbox.backity.gameproviders.gog.domain.GogGameWithFiles;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogLibraryService;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class GetGogGameDetailsUseCase {

    private final GogLibraryService gogLibraryService;

    public Optional<GogGameWithFiles> getGameDetails(String gameId) {
        return gogLibraryService.getGameDetails(gameId);
    }
}
