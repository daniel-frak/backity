package dev.codesoapbox.backity.core.sourcefile.domain;

import lombok.NonNull;

public record FileTitle(@NonNull String value) {

    @Override
    public String toString() {
        return value;
    }
}
