package dev.codesoapbox.backity.core.game.application.readmodel;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyFailureReason;
import dev.codesoapbox.backity.core.storagesolution.domain.FilePath;

import java.util.Optional;

public class TestFileCopyReadModel {

    public static FileCopyReadModel from(FileCopy fileCopy) {
        return new FileCopyReadModel(
                fileCopy.getId().value().toString(),
                new FileCopyNaturalIdReadModel(
                        fileCopy.getNaturalId().sourceFileId().value().toString(),
                        fileCopy.getNaturalId().backupTargetId().value().toString()
                ),
                fileCopy.getStatus(),
                Optional.ofNullable(fileCopy.getFailedReason())
                        .map(FileCopyFailureReason::value)
                        .orElse(null),
                Optional.ofNullable(fileCopy.getFilePath())
                        .map(FilePath::toString)
                        .orElse(null),
                fileCopy.getDateCreated(),
                fileCopy.getDateModified()
        );
    }
}