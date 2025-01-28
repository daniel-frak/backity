package dev.codesoapbox.backity.core.filemanagement.adapters.driven.filesystem;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Based on: <a href="https://gist.github.com/jcputney/b5daeb86a1c0696859da2a0c3b466327">Github gist</a>
 */
public class S3OutputStream extends OutputStream {

    /**
     * The temporary buffer used for storing the chunks
     */
    protected final byte[] temporaryBuffer;
    private final S3Client s3Client;
    /**
     * The bucket-name on Amazon S3
     */
    private final String bucket;
    /**
     * The object key within the bucket
     */
    private final String key;
    /**
     * Collection of the eTags for the parts that have been uploaded
     */
    private final List<String> partETags = new ArrayList<>();

    /**
     * The position in the buffer
     */
    private int positionInBuffer = 0;

    /**
     * The unique id for this upload
     */
    private String uploadId;

    /**
     * Indicates whether the stream is still open / valid
     */
    private boolean isOpen = true;

    public S3OutputStream(S3Client s3Client, String bucket, String key, int bufferSizeBytes) {
        this.key = key;
        this.bucket = bucket;
        this.s3Client = s3Client;
        this.temporaryBuffer = new byte[bufferSizeBytes];
    }

    public void cancel() {
        isOpen = false;
        if (uploadId != null) {
            abortMultipartUpload();
        }
    }

    private void abortMultipartUpload() {
        s3Client.abortMultipartUpload(AbortMultipartUploadRequest.builder()
                .bucket(bucket)
                .key(key)
                .uploadId(uploadId)
                .build());
    }

    @Override
    public void write(int byteToWrite) {
        assertOpen();
        if (positionInBuffer >= temporaryBuffer.length) {
            flushBufferAndRewind();
        }
        temporaryBuffer[positionInBuffer] = (byte) byteToWrite;
        positionInBuffer++;
    }

    private void assertOpen() {
        if (!isOpen) {
            throw new IllegalStateException("Closed");
        }
    }

    protected void flushBufferAndRewind() {
        if (uploadId == null) {
            CreateMultipartUploadResponse multipartUploadResponse = createMultipartUpload();
            uploadId = multipartUploadResponse.uploadId();
        }
        UploadPartResponse uploadPartResponse = uploadPart();
        partETags.add(uploadPartResponse.eTag());
        positionInBuffer = 0;
    }

    private CreateMultipartUploadResponse createMultipartUpload() {
        CreateMultipartUploadRequest uploadRequest = CreateMultipartUploadRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        return s3Client.createMultipartUpload(uploadRequest);
    }

    protected UploadPartResponse uploadPart() {
        UploadPartRequest uploadRequest = UploadPartRequest.builder()
                .bucket(bucket)
                .key(key)
                .uploadId(uploadId)
                .partNumber(partETags.size() + 1)
                .contentLength((long) positionInBuffer)
                .build();
        RequestBody requestBody = createRequestBody();

        return s3Client.uploadPart(uploadRequest, requestBody);
    }

    private RequestBody createRequestBody() {
        var inputStream = new ByteArrayInputStream(temporaryBuffer.clone(), 0, positionInBuffer);
        return RequestBody.fromInputStream(inputStream, positionInBuffer);
    }

    /**
     * Write an array to the S3 output stream.
     *
     * @param bytes the byte-array to append
     */
    @Override
    public void write(byte[] bytes) {
        write(bytes, 0, bytes.length);
    }

    /**
     * Writes an array to the S3 Output Stream
     *
     * @param bytesToWrite the array to write
     * @param offset       the offset into the array
     * @param length       the number of bytes to write
     */
    @Override
    public void write(byte[] bytesToWrite, int offset, int length) {
        assertOpen();
        int currentOffset = offset;
        int numOfBytesLeft = length;
        int numOfBytesInBuffer;
        while (numOfBytesLeft > (numOfBytesInBuffer = getNumOfBytesInBuffer())) { // If bytesToWrite exceeds buffer size
            copyChunkToBuffer(bytesToWrite, currentOffset,
                    numOfBytesInBuffer);
            currentOffset += numOfBytesInBuffer;
            numOfBytesLeft -= numOfBytesInBuffer;
            flushBufferAndRewind();
        }
        copyChunkToBuffer(bytesToWrite, currentOffset, numOfBytesLeft);
    }

    private void copyChunkToBuffer(byte[] bytesToWrite, int currentOffset, int numOfBytesToWrite) {
        System.arraycopy(bytesToWrite, currentOffset, temporaryBuffer, positionInBuffer, numOfBytesToWrite);
        positionInBuffer += numOfBytesToWrite;
    }

    private int getNumOfBytesInBuffer() {
        return temporaryBuffer.length - positionInBuffer;
    }

    /**
     * Flushes the buffer by uploading a part to S3.
     * <p>
     * Forces a multi-part upload.
     */
    @Override
    public synchronized void flush() {
        assertOpen();
        flushBufferAndRewind();
    }

    @Override
    public void close() {
        if (!isOpen) {
            return;
        }

        isOpen = false;
        if (uploadId != null) {
            if (positionInBuffer > 0) {
                uploadPart();
            }
            completeMultipartUpload();
        } else {
            performSinglePartUpload();
        }
    }

    private void completeMultipartUpload() {
        CompletedPart[] completedParts = getCompletedParts();

        s3Client.completeMultipartUpload(request -> request
                .bucket(bucket)
                .key(key)
                .uploadId(uploadId)
                .multipartUpload(multipartUpload -> multipartUpload.parts(completedParts)));
    }

    private CompletedPart[] getCompletedParts() {
        CompletedPart[] completedParts = new CompletedPart[partETags.size()];
        for (int i = 0; i < partETags.size(); i++) {
            completedParts[i] = CompletedPart.builder()
                    .eTag(partETags.get(i))
                    .partNumber(i + 1)
                    .build();
        }
        return completedParts;
    }

    private void performSinglePartUpload() {
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentLength((long) positionInBuffer)
                .build();

        RequestBody requestBody = createRequestBody();
        s3Client.putObject(putRequest, requestBody);
    }
}
