package dev.codesoapbox.backity.core.files.domain.downloading.services;

import dev.codesoapbox.backity.core.files.domain.downloading.model.FileStatus;
import dev.codesoapbox.backity.core.files.domain.downloading.model.GameFileVersion;
import dev.codesoapbox.backity.core.files.domain.downloading.repositories.GameFileVersionRepository;
import lombok.RequiredArgsConstructor;

import javax.transaction.Transactional;
import java.util.Optional;

@RequiredArgsConstructor
public class FileDownloadQueue {

    private final GameFileVersionRepository gameFileVersionRepository;
    private final FileDownloadMessageService messageService;

    @Transactional
    public void enqueue(GameFileVersion gameFileVersion) {

        gameFileVersion.setStatus(FileStatus.ENQUEUED_FOR_DOWNLOAD);
        gameFileVersionRepository.save(gameFileVersion);
    }

    // @TODO Remove this method?
    public Optional<GameFileVersion> getOldestWaiting() {
        return gameFileVersionRepository.findOldestWaitingForDownload();
    }

    public void acknowledgeSuccess(GameFileVersion gameFileVersion, String filePath) {
        gameFileVersion.markAsDownloaded(filePath);
        gameFileVersionRepository.save(gameFileVersion);
        messageService.sendDownloadFinished(gameFileVersion);
    }

    public void acknowledgeFailed(GameFileVersion gameFileVersion, String reason) {
        gameFileVersion.fail(reason);
        gameFileVersionRepository.save(gameFileVersion);
        messageService.sendDownloadFinished(gameFileVersion);
    }

    public void markInProgress(GameFileVersion gameFileVersion) {
        gameFileVersion.setStatus(FileStatus.DOWNLOAD_IN_PROGRESS);
        gameFileVersionRepository.save(gameFileVersion);
        messageService.sendDownloadStarted(gameFileVersion);
    }
}
