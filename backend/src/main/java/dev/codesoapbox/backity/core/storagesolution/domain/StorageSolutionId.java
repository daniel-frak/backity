package dev.codesoapbox.backity.core.storagesolution.domain;

public record StorageSolutionId(
        String value
) {

    @Override
    public String toString() {
        return value;
    }
}
