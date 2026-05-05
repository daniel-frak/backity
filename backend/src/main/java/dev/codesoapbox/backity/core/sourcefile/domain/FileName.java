package dev.codesoapbox.backity.core.sourcefile.domain;

import dev.codesoapbox.backity.shared.domain.exceptions.DomainValueIsEmptyException;
import lombok.NonNull;

public record FileName(@NonNull String value) {

    public FileName {
        if (value.isBlank()) {
            throw new DomainValueIsEmptyException("File name");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
