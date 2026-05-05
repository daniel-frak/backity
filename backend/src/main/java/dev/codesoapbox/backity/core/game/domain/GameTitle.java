package dev.codesoapbox.backity.core.game.domain;

import dev.codesoapbox.backity.shared.domain.exceptions.DomainValueIsEmptyException;
import lombok.NonNull;

public record GameTitle(@NonNull String value) {

    public GameTitle {
        if (value.isBlank()) {
            throw new DomainValueIsEmptyException("Game title");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
