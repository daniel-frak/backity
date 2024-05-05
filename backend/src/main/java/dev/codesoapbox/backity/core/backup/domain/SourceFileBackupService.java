package dev.codesoapbox.backity.core.backup.domain;

import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;

import java.io.IOException;

public interface SourceFileBackupService {

    FileSourceId getSource();

    /**
     * @return the path of the downloaded file
     */
    String backUpFile(FileDetails fileDetails, String tempFilePath) throws IOException;

    boolean isReady();
}
