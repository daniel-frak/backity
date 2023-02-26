package dev.codesoapbox.backity.core.files.domain.downloading.services;

import dev.codesoapbox.backity.core.files.domain.downloading.model.GameFileVersion;
import dev.codesoapbox.backity.core.files.domain.downloading.model.messages.FileDownloadProgress;

public interface FileDownloadMessageService {

    void sendDownloadStarted(GameFileVersion payload);

    void sendProgress(FileDownloadProgress payload);

    void sendDownloadFinished(GameFileVersion payload);
}
