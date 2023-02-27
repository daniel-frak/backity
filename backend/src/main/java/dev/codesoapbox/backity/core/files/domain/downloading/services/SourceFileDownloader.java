package dev.codesoapbox.backity.core.files.domain.downloading.services;

import dev.codesoapbox.backity.core.files.domain.downloading.model.GameFileVersion;

import java.io.IOException;

public interface SourceFileDownloader {

    String getSource();

    /**
     * @return the path of the downloaded file
     */
    String downloadGameFile(GameFileVersion gameFileVersion, String tempFilePath) throws IOException;

    boolean isReady();
}
