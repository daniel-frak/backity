package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.storagesolution.domain.FilePath;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
import lombok.NonNull;

public record WriteDestination(
        @NonNull StorageSolutionId storageSolutionId,
        @NonNull FilePath filePath
) implements Comparable<WriteDestination> {

    @Override
    public int compareTo(WriteDestination other) {
        int storageSolutionCompare = this.storageSolutionId.compareTo(other.storageSolutionId);
        if (storageSolutionCompare != 0) {
            return storageSolutionCompare;
        }
        return this.filePath.toString().compareTo(other.filePath.toString());
    }
}
