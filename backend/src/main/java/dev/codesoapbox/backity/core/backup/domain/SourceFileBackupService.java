package dev.codesoapbox.backity.core.backup.domain;

import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetails;

import java.io.IOException;

public interface SourceFileBackupService {

    FileSourceId getSource();

    /**
     * @return the path of the downloaded file
     */
    String backUpGameFile(GameFileDetails gameFileDetails, String tempFilePath) throws IOException;

    boolean isReady();
}
