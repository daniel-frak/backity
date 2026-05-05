package dev.codesoapbox.backity.core.sourcefile.domain;

import dev.codesoapbox.backity.shared.domain.exceptions.DomainValueIsEmptyException;
import lombok.NonNull;

public record FileTitle(@NonNull String value) {

    public FileTitle {
        if (value.isBlank()) {
            throw new DomainValueIsEmptyException("File title");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
