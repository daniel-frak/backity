package dev.codesoapbox.backity.integrations.gog.application;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetGogConfigUseCase {

    private final GogConfigInfo gogConfigInfo;

    public GogConfigInfo getGogConfig() {
        return gogConfigInfo;
    }
}
