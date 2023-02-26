package dev.codesoapbox.backity.core.files.domain.downloading.exceptions;

import dev.codesoapbox.backity.core.files.domain.downloading.model.GameFileVersion;

public class FileDownloadFailedException extends RuntimeException {

    public FileDownloadFailedException(GameFileVersion gameFileVersion, Throwable cause) {
        super("Could not download game file " + gameFileVersion.getId(), cause);
    }
}
