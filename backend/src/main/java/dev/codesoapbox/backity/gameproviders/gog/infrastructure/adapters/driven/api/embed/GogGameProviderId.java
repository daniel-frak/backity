package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.embed;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GogGameProviderId {

    private static final GameProviderId GAME_PROVIDER_ID = new GameProviderId("GOG");

    public static GameProviderId get() {
        return GAME_PROVIDER_ID;
    }
}
