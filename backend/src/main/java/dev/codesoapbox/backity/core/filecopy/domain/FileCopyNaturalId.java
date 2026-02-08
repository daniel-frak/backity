package dev.codesoapbox.backity.core.filecopy.domain;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFileId;
import lombok.NonNull;

public record FileCopyNaturalId(
        @NonNull SourceFileId sourceFileId,
        @NonNull BackupTargetId backupTargetId
) implements Comparable<FileCopyNaturalId> {

    @Override
    public int compareTo(FileCopyNaturalId other) {
        int sourceFileComparison = this.sourceFileId.compareTo(other.sourceFileId);
        return sourceFileComparison != 0
                ? sourceFileComparison
                : this.backupTargetId.compareTo(other.backupTargetId);
    }
}
