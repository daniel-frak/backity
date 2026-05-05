package dev.codesoapbox.backity.core.sourcefile.domain;

import dev.codesoapbox.backity.shared.domain.exceptions.DomainValueIsEmptyException;
import lombok.NonNull;

public record SourceFileUrl(@NonNull String value) {

    public SourceFileUrl {
        if (value.isBlank()) {
            throw new DomainValueIsEmptyException("Source file url");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
