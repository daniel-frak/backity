package dev.codesoapbox.backity.core.game.application.readmodel;

public record FileCopyNaturalIdReadModel(
        String sourceFileId,
        String backupTargetId
) {
}
