package dev.codesoapbox.backity.core.files.domain.backup.services;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersion;

import java.io.IOException;

public interface SourceFileBackupService {

    String getSource();

    /**
     * @return the path of the downloaded file
     */
    String backUpGameFile(GameFileVersion gameFileVersion, String tempFilePath) throws IOException;

    boolean isReady();
}
