package dev.codesoapbox.backity.core.backup.domain;

import dev.codesoapbox.backity.core.backup.domain.exceptions.FileBackupFailedException;
import dev.codesoapbox.backity.core.backup.domain.exceptions.FileBackupUrlEmptyException;
import dev.codesoapbox.backity.core.backup.domain.exceptions.NotEnoughFreeSpaceException;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.filemanagement.domain.FileManager;
import dev.codesoapbox.backity.core.filemanagement.domain.FilePathProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Wrapper for all gameProviderId file backup services.
 * <p>
 * Downloads files from remote servers.
 */
@Slf4j
public class FileBackupService {

    private final FilePathProvider filePathProvider;
    private final GameFileRepository gameFileRepository;
    private final FileManager fileManager;
    private final Map<GameProviderId, GameProviderFileBackupService> gameProviderFileBackupServices;

    public FileBackupService(FilePathProvider filePathProvider, GameFileRepository gameFileRepository,
                             FileManager fileManager,
                             List<GameProviderFileBackupService> gameProviderFileBackupServices) {
        this.filePathProvider = filePathProvider;
        this.gameFileRepository = gameFileRepository;
        this.fileManager = fileManager;
        this.gameProviderFileBackupServices = gameProviderFileBackupServices.stream()
                .collect(Collectors.toMap(GameProviderFileBackupService::getGameProviderId, d -> d));
    }

    public void backUpFile(GameFile gameFile) {
        log.info("Backing up game file {} (url={})...", gameFile.getId(),
                gameFile.getGameProviderFile().url());

        try {
            markInProgress(gameFile);
            validateReadyForDownload(gameFile);
            String tempFilePath = createTemporaryFilePath(gameFile);
            validateEnoughFreeSpaceOnDisk(tempFilePath, gameFile.getGameProviderFile().size());
            tryToBackUp(gameFile, tempFilePath);
        } catch (IOException | RuntimeException e) {
            markFailed(gameFile, e);
            throw new FileBackupFailedException(gameFile, e);
        }
    }

    private void markInProgress(GameFile gameFile) {
        gameFile.markAsInProgress();
        gameFileRepository.save(gameFile);
    }

    private void validateReadyForDownload(GameFile gameFile) {
        if (Strings.isBlank(gameFile.getGameProviderFile().url())) {
            throw new FileBackupUrlEmptyException(gameFile.getId());
        }
    }

    private String createTemporaryFilePath(GameFile gameFile) throws IOException {
        return filePathProvider.createTemporaryFilePath(
                gameFile.getGameProviderFile().gameProviderId(),
                gameFile.getGameProviderFile().originalGameTitle());
    }

    private void validateEnoughFreeSpaceOnDisk(String filePath, String size) {
        Long sizeInBytes = new FileSizeAccumulator().add(size).getInBytes();
        if (!fileManager.isEnoughFreeSpaceOnDisk(sizeInBytes, filePath)) {
            throw new NotEnoughFreeSpaceException(filePath);
        }
    }

    private void tryToBackUp(GameFile gameFile, String tempFilePath) throws IOException {
        try {
            updateFilePath(gameFile, tempFilePath);
            String downloadedPath = downloadToDisk(gameFile, tempFilePath);
            markDownloaded(gameFile, downloadedPath);
        } catch (IOException e) {
            tryToCleanUpAfterFailedDownload(gameFile, tempFilePath);
            throw e;
        }
    }

    private void updateFilePath(GameFile gameFile, String tempFilePath) {
        gameFile.updateFilePath(tempFilePath);
        gameFileRepository.save(gameFile);
    }

    /**
     * @return the path of the downloaded file
     */
    private String downloadToDisk(GameFile gameFile, String tempFilePath) throws IOException {
        GameProviderId gameProviderId = gameFile.getGameProviderFile().gameProviderId();
        GameProviderFileBackupService gameProviderFileBackupService = getGameProviderFileBackupService(gameProviderId);
        return gameProviderFileBackupService.backUpFile(gameFile, tempFilePath);
    }

    private void markDownloaded(GameFile gameFile, String downloadedPath) {
        gameFile.markAsDownloaded(downloadedPath);
        gameFileRepository.save(gameFile);
    }

    private void tryToCleanUpAfterFailedDownload(GameFile gameFile,
                                                 String tempFilePath) throws IOException {
        fileManager.deleteIfExists(tempFilePath);
        if (tempFilePath.equals(gameFile.getFileBackup().getFilePath())) {
            gameFile.clearFilePath();
            gameFileRepository.save(gameFile);
        }
    }

    private GameProviderFileBackupService getGameProviderFileBackupService(GameProviderId gameProviderId) {
        if (!gameProviderFileBackupServices.containsKey(gameProviderId)) {
            throw new IllegalArgumentException("File backup service for gameProviderId not found: " + gameProviderId);
        }

        return gameProviderFileBackupServices.get(gameProviderId);
    }

    private void markFailed(GameFile gameFile, Exception e) {
        gameFile.fail(e.getMessage());
        gameFileRepository.save(gameFile);
    }

    public boolean isReadyFor(GameFile gameFile) {
        return getGameProviderFileBackupService(gameFile.getGameProviderFile().gameProviderId()).isReady();
    }
}
