package dev.codesoapbox.backity.core.game.application;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgress;
import dev.codesoapbox.backity.core.game.application.readmodel.GameWithFileCopiesReadModel;
import lombok.NonNull;

import java.util.List;

public record GameWithFileCopiesAndReplicationProgresses(
        @NonNull GameWithFileCopiesReadModel gameWithFileCopies,
        @NonNull List<FileCopyReplicationProgress> replicationProgresses
) {
}
