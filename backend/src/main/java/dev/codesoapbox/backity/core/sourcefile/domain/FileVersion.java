package dev.codesoapbox.backity.core.sourcefile.domain;

import lombok.NonNull;

public record FileVersion(@NonNull String value) {

    @Override
    public String toString() {
        return value;
    }
}
