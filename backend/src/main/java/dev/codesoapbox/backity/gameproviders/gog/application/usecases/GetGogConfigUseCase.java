package dev.codesoapbox.backity.gameproviders.gog.application.usecases;

import dev.codesoapbox.backity.gameproviders.gog.application.GogConfigInfo;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetGogConfigUseCase {

    private final GogConfigInfo gogConfigInfo;

    public GogConfigInfo getGogConfig() {
        return gogConfigInfo;
    }
}
