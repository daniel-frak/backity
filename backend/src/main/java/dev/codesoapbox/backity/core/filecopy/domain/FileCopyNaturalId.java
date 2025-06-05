package dev.codesoapbox.backity.core.filecopy.domain;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import lombok.NonNull;

public record FileCopyNaturalId(
        @NonNull GameFileId gameFileId,
        @NonNull BackupTargetId backupTargetId
) implements Comparable<FileCopyNaturalId> {

    @Override
    public int compareTo(FileCopyNaturalId other) {
        int gameFileComparison = this.gameFileId.compareTo(other.gameFileId);
        return gameFileComparison != 0
                ? gameFileComparison
                : this.backupTargetId.compareTo(other.backupTargetId);
    }
}
