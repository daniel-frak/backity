package dev.codesoapbox.backity.core.storagesolution.infrastructure.adapters.driven.filesystem;

import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.FileResource;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
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
    @SuppressWarnings("java:S1166")
    public boolean fileExists(String key) {
        try {
            s3Client.headObject(request -> request.bucket(bucketName).key(key));
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }

    @Override
    public StorageSolutionId getId() {
        return ID;
    }

    @Override
    public OutputStream getOutputStream(String stringPath) throws FileAlreadyExistsException {
        if(fileExists(stringPath)) {
            throw new FileAlreadyExistsException(stringPath);
        }
        return new S3OutputStream(s3Client, bucketName, stringPath, bufferSizeInBytes);
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

    @Override
    public String getSeparator() {
        return "/";
    }
}
