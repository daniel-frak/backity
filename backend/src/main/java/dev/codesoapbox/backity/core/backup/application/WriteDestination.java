package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
import lombok.NonNull;

public record WriteDestination(
        @NonNull StorageSolutionId storageSolutionId,
        @NonNull String filePath
) implements Comparable<WriteDestination> {

    @Override
    public int compareTo(WriteDestination other) {
        int storageSolutionCompare = this.storageSolutionId.compareTo(other.storageSolutionId);
        if (storageSolutionCompare != 0) {
            return storageSolutionCompare;
        }
        return this.filePath.compareTo(other.filePath);
    }
}
