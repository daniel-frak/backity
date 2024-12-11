package dev.codesoapbox.backity.core.backup.domain;

import lombok.NonNull;

import java.util.Objects;

public record GameProviderId(@NonNull String value) implements Comparable<GameProviderId> {

    @Override
    public int compareTo(GameProviderId gameProviderId) {
        return Objects.compare(value, gameProviderId.value(), String::compareTo);
    }

    @Override
    public String toString() {
        return value;
    }
}
