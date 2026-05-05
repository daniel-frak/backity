package dev.codesoapbox.backity.core.sourcefile.domain;

import dev.codesoapbox.backity.shared.domain.exceptions.DomainValueIsEmptyException;
import lombok.NonNull;

public record FileVersion(@NonNull String value) {

    public FileVersion {
        if (value.isBlank()) {
            throw new DomainValueIsEmptyException("File version");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
