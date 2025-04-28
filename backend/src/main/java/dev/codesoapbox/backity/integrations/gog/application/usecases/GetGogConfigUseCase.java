package dev.codesoapbox.backity.integrations.gog.application.usecases;

import dev.codesoapbox.backity.integrations.gog.application.GogConfigInfo;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetGogConfigUseCase {

    private final GogConfigInfo gogConfigInfo;

    public GogConfigInfo getGogConfig() {
        return gogConfigInfo;
    }
}
