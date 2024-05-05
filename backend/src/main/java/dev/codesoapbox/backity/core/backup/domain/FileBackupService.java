package dev.codesoapbox.backity.core.backup.domain;

import dev.codesoapbox.backity.core.backup.domain.exceptions.FileBackupFailedException;
import dev.codesoapbox.backity.core.backup.domain.exceptions.FileBackupUrlEmptyException;
import dev.codesoapbox.backity.core.backup.domain.exceptions.NotEnoughFreeSpaceException;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetailsRepository;
import dev.codesoapbox.backity.core.filemanagement.domain.FileManager;
import dev.codesoapbox.backity.core.filemanagement.domain.FilePathProvider;
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
    private final FileDetailsRepository fileDetailsRepository;
    private final FileManager fileManager;
    private final Map<FileSourceId, SourceFileBackupService> sourceFileDownloaders;

    public FileBackupService(FilePathProvider filePathProvider, FileDetailsRepository fileDetailsRepository,
                             FileManager fileManager, List<SourceFileBackupService> sourceFileBackupServices) {
        this.filePathProvider = filePathProvider;
        this.fileDetailsRepository = fileDetailsRepository;
        this.fileManager = fileManager;
        this.sourceFileDownloaders = sourceFileBackupServices.stream()
                .collect(Collectors.toMap(SourceFileBackupService::getSource, d -> d));
    }

    public void backUpFile(FileDetails fileDetails) {
        log.info("Backing up game file {} (url={})...", fileDetails.getId(),
                fileDetails.getSourceFileDetails().url());

        try {
            markInProgress(fileDetails);
            validateReadyForDownload(fileDetails);
            String tempFilePath = createTemporaryFilePath(fileDetails);
            validateEnoughFreeSpaceOnDisk(tempFilePath, fileDetails.getSourceFileDetails().size());
            tryToBackUp(fileDetails, tempFilePath);
        } catch (IOException | RuntimeException e) {
            markFailed(fileDetails, e);
            throw new FileBackupFailedException(fileDetails, e);
        }
    }

    private void markInProgress(FileDetails fileDetails) {
        fileDetails.markAsInProgress();
        fileDetailsRepository.save(fileDetails);
    }

    private void validateReadyForDownload(FileDetails fileDetails) {
        if (Strings.isBlank(fileDetails.getSourceFileDetails().url())) {
            throw new FileBackupUrlEmptyException(fileDetails.getId());
        }
    }

    private String createTemporaryFilePath(FileDetails fileDetails) throws IOException {
        return filePathProvider.createTemporaryFilePath(
                fileDetails.getSourceFileDetails().sourceId(),
                fileDetails.getSourceFileDetails().originalGameTitle());
    }

    private void validateEnoughFreeSpaceOnDisk(String filePath, String size) {
        Long sizeInBytes = new FileSizeAccumulator().add(size).getInBytes();
        if (!fileManager.isEnoughFreeSpaceOnDisk(sizeInBytes, filePath)) {
            throw new NotEnoughFreeSpaceException(filePath);
        }
    }

    private void tryToBackUp(FileDetails fileDetails, String tempFilePath) throws IOException {
        try {
            updateFilePath(fileDetails, tempFilePath);
            String downloadedPath = downloadToDisk(fileDetails, tempFilePath);
            markDownloaded(fileDetails, downloadedPath);
        } catch (IOException e) {
            tryToCleanUpAfterFailedDownload(fileDetails, tempFilePath);
            throw e;
        }
    }

    private void updateFilePath(FileDetails fileDetails, String tempFilePath) {
        fileDetails.updateFilePath(tempFilePath);
        fileDetailsRepository.save(fileDetails);
    }

    /**
     * @return the path of the downloaded file
     */
    private String downloadToDisk(FileDetails fileDetails, String tempFilePath) throws IOException {
        FileSourceId sourceId = fileDetails.getSourceFileDetails().sourceId();
        SourceFileBackupService sourceDownloader = getSourceDownloader(sourceId);
        return sourceDownloader.backUpFile(fileDetails, tempFilePath);
    }

    private void markDownloaded(FileDetails fileDetails, String downloadedPath) {
        fileDetails.markAsDownloaded(downloadedPath);
        fileDetailsRepository.save(fileDetails);
    }

    private void tryToCleanUpAfterFailedDownload(FileDetails fileDetails,
                                                 String tempFilePath) throws IOException {
        fileManager.deleteIfExists(tempFilePath);
        if (tempFilePath.equals(fileDetails.getBackupDetails().getFilePath())) {
            fileDetails.clearFilePath();
            fileDetailsRepository.save(fileDetails);
        }
    }

    private SourceFileBackupService getSourceDownloader(FileSourceId sourceId) {
        if (!sourceFileDownloaders.containsKey(sourceId)) {
            throw new IllegalArgumentException("File downloader for sourceId not found: " + sourceId);
        }

        return sourceFileDownloaders.get(sourceId);
    }

    private void markFailed(FileDetails fileDetails, Exception e) {
        fileDetails.fail(e.getMessage());
        fileDetailsRepository.save(fileDetails);
    }

    public boolean isReadyFor(FileDetails fileDetails) {
        return getSourceDownloader(fileDetails.getSourceFileDetails().sourceId()).isReady();
    }
}
