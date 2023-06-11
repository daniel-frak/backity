package dev.codesoapbox.backity.core.files.domain.backup.model;

import java.util.UUID;

public record GameFileDetailsId(
        UUID value
) {
    public static GameFileDetailsId newInstance() {
        return new GameFileDetailsId(UUID.randomUUID());
    }
}
