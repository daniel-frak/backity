package dev.codesoapbox.backity.core.storagesolution.infrastructure.adapters.driven.filesystem;

import dev.codesoapbox.backity.core.storagesolution.domain.*;
import dev.codesoapbox.backity.core.storagesolution.domain.exceptions.FileCouldNotBeDeletedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;

@Slf4j
@RequiredArgsConstructor
public class S3StorageSolution implements StorageSolution {

    public static final StorageSolutionId ID = new StorageSolutionId("S3");

    private final S3Client s3Client;
    private final String bucketName;
    private final int bufferSizeInBytes;

    @Override
    public StorageSolutionStatus getStatus() {
        try {
            s3Client.headBucket(request -> request.bucket(bucketName));
            return StorageSolutionStatus.CONNECTED;
        } catch (RuntimeException e) {
            log.debug("S3 storage solution connectivity check failed", e);
            return StorageSolutionStatus.NOT_CONNECTED;
        }
    }

    @Override
    public StorageSolutionId getId() {
        return ID;
    }

    @Override
    public OutputStream getOutputStream(FilePath filePath) throws FileAlreadyExistsException {
        if (fileExists(filePath)) {
            throw new FileAlreadyExistsException(filePath.toString());
        }
        return new S3OutputStream(s3Client, bucketName, filePath.toString(), bufferSizeInBytes);
    }

    @Override
    @SuppressWarnings("java:S1166")
    public boolean fileExists(FilePath filePath) {
        try {
            s3Client.headObject(request -> request.bucket(bucketName).key(filePath.toString()));
            return true;
        } catch (NoSuchKeyException _) {
            return false;
        }
    }

    @Override
    public void deleteIfExists(FilePath filePath) {
        try {
            s3Client.deleteObject(request -> request.bucket(bucketName).key(filePath.toString()));
        } catch (RuntimeException e) {
            throw new FileCouldNotBeDeletedException(filePath, e);
        }
    }

    @SuppressWarnings("java:S1166")
    @Override
    public long getSizeInBytes(FilePath filePath) {
        try {
            return s3Client.headObject(request -> request.bucket(bucketName).key(filePath.toString()))
                    .contentLength();
        } catch (NoSuchKeyException _) {
            return 0L;
        }
    }

    @SuppressWarnings("java:S1166")
    @Override
    public FileResource getFileResource(FilePath filePath) throws FileNotFoundException {
        try {
            return tryToGetFileResource(filePath);
        } catch (NoSuchKeyException _) {
            throw new FileNotFoundException("File not found: " + filePath.toString());
        }
    }

    private FileResource tryToGetFileResource(FilePath filePath) {
        ResponseInputStream<GetObjectResponse> responseInputStream = s3Client.getObject(
                request -> request.bucket(bucketName).key(filePath.toString())
        );

        return new FileResource(
                responseInputStream,
                responseInputStream.response().contentLength(),
                filePath.toString().substring(filePath.toString().lastIndexOf(getSeparator()) + 1)
        );
    }

    @Override
    public String getSeparator() {
        return "/";
    }
}
