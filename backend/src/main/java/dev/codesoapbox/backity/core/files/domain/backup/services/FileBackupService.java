package dev.codesoapbox.backity.core.files.domain.backup.services;

import dev.codesoapbox.backity.core.files.domain.backup.exceptions.FileBackupFailedException;
import dev.codesoapbox.backity.core.files.domain.backup.exceptions.FileBackupUrlEmptyException;
import dev.codesoapbox.backity.core.files.domain.backup.exceptions.NotEnoughFreeSpaceException;
import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Wrapper for all sourceId file downloaders.
 * <p>
 * Downloads files from remote servers.
 */
@Slf4j
public class FileBackupService {

    private final FilePathProvider filePathProvider;
    private final GameFileDetailsRepository gameFileDetailsRepository;
    private final FileManager fileManager;
    private final Map<String, SourceFileBackupService> sourceFileDownloaders;

    public FileBackupService(FilePathProvider filePathProvider, GameFileDetailsRepository gameFileDetailsRepository,
                             FileManager fileManager, List<SourceFileBackupService> sourceFileBackupServices) {
        this.filePathProvider = filePathProvider;
        this.gameFileDetailsRepository = gameFileDetailsRepository;
        this.fileManager = fileManager;
        this.sourceFileDownloaders = sourceFileBackupServices.stream()
                .collect(Collectors.toMap(SourceFileBackupService::getSource, d -> d));
    }

    public void backUpGameFile(GameFileDetails gameFileDetails) {
        log.info("Backing up game file {} (url={})...", gameFileDetails.getId(), gameFileDetails.getUrl());

        try {
            markInProgress(gameFileDetails);
            validateReadyForDownload(gameFileDetails);
            String tempFilePath = createTemporaryFilePath(gameFileDetails);
            validateEnoughFreeSpaceOnDisk(tempFilePath, gameFileDetails.getSize());
            tryToBackUp(gameFileDetails, tempFilePath);
        } catch (IOException | RuntimeException e) {
            markFailed(gameFileDetails, e);
            throw new FileBackupFailedException(gameFileDetails, e);
        }
    }

    private void markInProgress(GameFileDetails gameFileDetails) {
        gameFileDetails.setBackupStatus(FileBackupStatus.IN_PROGRESS);
        gameFileDetailsRepository.save(gameFileDetails);
    }

    private void validateReadyForDownload(GameFileDetails gameFileDetails) {
        if (Strings.isBlank(gameFileDetails.getUrl())) {
            throw new FileBackupUrlEmptyException(gameFileDetails.getId());
        }
    }

    private String createTemporaryFilePath(GameFileDetails gameFileDetails) throws IOException {
        return filePathProvider.createTemporaryFilePath(
                gameFileDetails.getSource(), gameFileDetails.getGameTitle());
    }

    private void validateEnoughFreeSpaceOnDisk(String filePath, String size) {
        // @TODO Get free up-to-date filesize from URL header!
        Long sizeInBytes = new FileSizeAccumulator().add(size).getInBytes();
        if (!fileManager.isEnoughFreeSpaceOnDisk(sizeInBytes, filePath)) {
            throw new NotEnoughFreeSpaceException(filePath);
        }
    }

    private void tryToBackUp(GameFileDetails gameFileDetails, String tempFilePath) throws IOException {
        try {
            updateFilePath(gameFileDetails, tempFilePath);
            String downloadedPath = downloadToDisk(gameFileDetails, tempFilePath);
            markDownloaded(gameFileDetails, downloadedPath);
        } catch (IOException e) {
            tryToCleanUpAfterFailedDownload(gameFileDetails, tempFilePath);
            throw e;
        }
    }

    private void updateFilePath(GameFileDetails gameFileDetails, String tempFilePath) {
        gameFileDetails.setFilePath(tempFilePath);
        gameFileDetailsRepository.save(gameFileDetails);
    }

    /**
     * @return the path of the downloaded file
     */
    private String downloadToDisk(GameFileDetails gameFileDetails, String tempFilePath) throws IOException {
        SourceFileBackupService sourceDownloader = getSourceDownloader(gameFileDetails.getSource());
        return sourceDownloader.backUpGameFile(gameFileDetails, tempFilePath);
    }

    private void markDownloaded(GameFileDetails gameFileDetails, String downloadedPath) {
        gameFileDetails.markAsDownloaded(downloadedPath);
        gameFileDetailsRepository.save(gameFileDetails);
    }

    private void tryToCleanUpAfterFailedDownload(GameFileDetails gameFileDetails,
                                                 String tempFilePath) throws IOException {
        fileManager.deleteIfExists(tempFilePath);
        if (tempFilePath.equals(gameFileDetails.getFilePath())) {
            gameFileDetails.setFilePath(null);
            gameFileDetailsRepository.save(gameFileDetails);
        }
    }

    private SourceFileBackupService getSourceDownloader(String source) {
        if (!sourceFileDownloaders.containsKey(source)) {
            throw new IllegalArgumentException("File downloader for sourceId not found: " + source);
        }

        return sourceFileDownloaders.get(source);
    }

    private void markFailed(GameFileDetails gameFileDetails, Exception e) {
        gameFileDetails.fail(e.getMessage());
        gameFileDetailsRepository.save(gameFileDetails);
    }

    public boolean isReadyFor(GameFileDetails gameFileDetails) {
        return getSourceDownloader(gameFileDetails.getSource()).isReady();
    }
}
