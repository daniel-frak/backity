package dev.codesoapbox.backity.core.storagesolution.domain;

import dev.codesoapbox.backity.shared.domain.exceptions.DomainValueIsEmptyException;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = false)
public final class FilePath {

    private final @NonNull String value;

    public FilePath(@NonNull String value) {
        if (value.isBlank()) {
            throw new DomainValueIsEmptyException("File path");
        }
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
