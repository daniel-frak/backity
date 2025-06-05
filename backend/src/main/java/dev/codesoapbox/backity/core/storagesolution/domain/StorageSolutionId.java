package dev.codesoapbox.backity.core.storagesolution.domain;

public record StorageSolutionId(
        String value
) implements Comparable<StorageSolutionId> {

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int compareTo(StorageSolutionId other) {
        return value.compareTo(other.value);
    }
}
