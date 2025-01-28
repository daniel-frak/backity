package dev.codesoapbox.backity.core.filemanagement.adapters.driven.filesystem;

import dev.codesoapbox.backity.core.filemanagement.domain.FileManager;
import dev.codesoapbox.backity.core.filemanagement.domain.FileResource;
import dev.codesoapbox.backity.core.filemanagement.domain.exceptions.FileCouldNotBeDeletedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.io.FileNotFoundException;
import java.io.OutputStream;

@Slf4j
@RequiredArgsConstructor
public class S3FileSystem implements FileManager {

    private final S3Client s3Client;
    private final String bucketName;

    @Override
    public String renameFileAddingSuffixIfExists(String fullFilePath, String targetFileName) {
        String directory = extractDirectory(fullFilePath);
        String uniqueTargetFileName = getUniqueFileName(directory, targetFileName);
        String destinationKey = directory + getSeparator() + uniqueTargetFileName;
        s3Client.copyObject(request -> request
                .sourceBucket(bucketName)
                .sourceKey(fullFilePath)
                .destinationBucket(bucketName)
                .destinationKey(destinationKey)
        );
        s3Client.deleteObject(request -> request.bucket(bucketName).key(fullFilePath));
        log.info("Renamed file {} to {}", fullFilePath, destinationKey);

        return destinationKey;
    }

    private String extractDirectory(String path) {
        return path.substring(0, path.lastIndexOf(getSeparator()));
    }

    @Override
    public String getSeparator() {
        return "/";
    }

    private String getUniqueFileName(String directory, String fileName) {
        String baseName = getBaseName(fileName);
        String targetBaseName = baseName;
        String extension = fileName.substring(baseName.length());
        int counter = 1;

        while (exists(directory + getSeparator() + targetBaseName + extension)) {
            targetBaseName = baseName + "_" + counter;
            counter++;
        }

        return targetBaseName + extension;
    }

    private String getBaseName(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex != -1) {
            return fileName.substring(0, dotIndex);
        }
        return fileName;
    }

    @SuppressWarnings("java:S1166")
    private boolean exists(String key) {
        try {
            s3Client.headObject(request -> request.bucket(bucketName).key(key));
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }

    @Override
    public OutputStream getOutputStream(String stringPath) {
        return new S3OutputStream(s3Client, bucketName, stringPath, 10_000_000);
    }

    @Override
    public void deleteIfExists(String path) {
        try {
            s3Client.deleteObject(request -> request.bucket(bucketName).key(path));
        } catch (RuntimeException e) {
            throw new FileCouldNotBeDeletedException(path, e);
        }
    }

    @SuppressWarnings("java:S1166")
    @Override
    public long getSizeInBytes(String filePath) {
        try {
            return s3Client.headObject(request -> request.bucket(bucketName).key(filePath)).contentLength();
        } catch (NoSuchKeyException e) {
            return 0L;
        }
    }

    @SuppressWarnings("java:S1166")
    @Override
    public FileResource getFileResource(String filePath) throws FileNotFoundException {
        try {
            return tryToGetFileResource(filePath);
        } catch (NoSuchKeyException e) {
            throw new FileNotFoundException("File not found: " + filePath);
        }
    }

    private FileResource tryToGetFileResource(String filePath) {
        ResponseInputStream<GetObjectResponse> responseInputStream = s3Client.getObject(
                request -> request.bucket(bucketName).key(filePath)
        );

        return new FileResource(
                responseInputStream,
                responseInputStream.response().contentLength(),
                filePath.substring(filePath.lastIndexOf(getSeparator()) + 1)
        );
    }
}
