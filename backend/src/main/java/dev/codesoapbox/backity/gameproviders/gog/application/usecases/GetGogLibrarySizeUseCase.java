package dev.codesoapbox.backity.gameproviders.gog.application.usecases;

import dev.codesoapbox.backity.gameproviders.gog.domain.GogLibraryService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetGogLibrarySizeUseCase {

    private final GogLibraryService gogLibraryService;

    public String getLibrarySize() {
        return gogLibraryService.getLibrarySize();
    }
}
