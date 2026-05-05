package dev.codesoapbox.backity.core.filecopy.domain;

import dev.codesoapbox.backity.shared.domain.exceptions.DomainValueIsEmptyException;
import lombok.NonNull;

public record FileCopyFailureReason(@NonNull String value) {

    public FileCopyFailureReason {
        if (value.isBlank()) {
            throw new DomainValueIsEmptyException("File copy failure reason");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
