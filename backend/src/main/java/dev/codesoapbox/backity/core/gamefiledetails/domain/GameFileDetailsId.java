package dev.codesoapbox.backity.core.gamefiledetails.domain;

import java.util.UUID;

public record GameFileDetailsId(
        UUID value
) {
    public static GameFileDetailsId newInstance() {
        return new GameFileDetailsId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
