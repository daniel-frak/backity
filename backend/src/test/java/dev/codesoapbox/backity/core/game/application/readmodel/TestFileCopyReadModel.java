package dev.codesoapbox.backity.core.game.application.readmodel;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;

public class TestFileCopyReadModel {

    public static FileCopyReadModel from(FileCopy fileCopy) {
        return new FileCopyReadModel(
                fileCopy.getId().value().toString(),
                new FileCopyNaturalIdReadModel(
                        fileCopy.getNaturalId().gameFileId().value().toString(),
                        fileCopy.getNaturalId().backupTargetId().value().toString()
                ),
                fileCopy.getStatus(),
                fileCopy.getFailedReason(),
                fileCopy.getFilePath(),
                fileCopy.getDateCreated(),
                fileCopy.getDateModified()
        );
    }
}