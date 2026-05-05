package dev.codesoapbox.backity.core.storagesolution.domain;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public final class FilePath {

    private final @NonNull String value;

    @Override
    public String toString() {
        return value;
    }
}
