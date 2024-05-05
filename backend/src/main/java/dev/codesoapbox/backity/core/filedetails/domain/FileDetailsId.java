package dev.codesoapbox.backity.core.filedetails.domain;

import java.util.UUID;

public record FileDetailsId(
        UUID value
) {
    public static FileDetailsId newInstance() {
        return new FileDetailsId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
