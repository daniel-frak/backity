package dev.codesoapbox.backity.core.gamefile.domain;

import java.util.UUID;

public record GameFileId(
        UUID value
) {
    public static GameFileId newInstance() {
        return new GameFileId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
