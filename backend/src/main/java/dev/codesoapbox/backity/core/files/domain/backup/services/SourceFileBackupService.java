package dev.codesoapbox.backity.core.files.domain.backup.services;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersionBackup;

import java.io.IOException;

public interface SourceFileBackupService {

    String getSource();

    /**
     * @return the path of the downloaded file
     */
    String backUpGameFile(GameFileVersionBackup gameFileVersionBackup, String tempFilePath) throws IOException;

    boolean isReady();
}
