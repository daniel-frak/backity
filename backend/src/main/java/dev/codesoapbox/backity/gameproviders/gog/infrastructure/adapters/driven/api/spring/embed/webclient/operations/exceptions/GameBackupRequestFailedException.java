package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.webclient.operations
        .exceptions;

import dev.codesoapbox.backity.core.sourcefile.domain.SourceFileUrl;

public class GameBackupRequestFailedException extends RuntimeException {

    public GameBackupRequestFailedException(SourceFileUrl fileUrl, Throwable cause) {
        super("An error occurred while backing up file: " + fileUrl, cause);
    }

    public GameBackupRequestFailedException(SourceFileUrl fileUrl, String message) {
        super("An error occurred while backing up file: " + fileUrl + ". " + message);
    }
}
