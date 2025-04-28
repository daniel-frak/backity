package dev.codesoapbox.backity.core.filemanagement.infrastructure.adapters.driven.filesystem;

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
        if (isMultiPartUpload()) {
            sendAbortMultipartUploadRequest();
        }
    }

    private boolean isMultiPartUpload() {
        return uploadId != null;
    }

    private void sendAbortMultipartUploadRequest() {
        s3Client.abortMultipartUpload(AbortMultipartUploadRequest.builder()
                .bucket(bucket)
                .key(key)
                .uploadId(uploadId)
                .build());
    }

    @Override
    public void write(int byteToWrite) {
        assertOpen();
        if (bufferIsOverflowing()) {
            flushBuffer();
        }
        writeToBuffer((byte) byteToWrite);
    }

    private void assertOpen() {
        if (!isOpen) {
            throw new IllegalStateException("Stream closed");
        }
    }

    private boolean bufferIsOverflowing() {
        return positionInBuffer >= temporaryBuffer.length;
    }

    protected void flushBuffer() {
        if (uploadId == null) {
            CreateMultipartUploadResponse multipartUploadResponse = sendCreateMultipartUploadRequest();
            uploadId = multipartUploadResponse.uploadId();
        }
        uploadPart();
    }

    private void writeToBuffer(byte byteToWrite) {
        temporaryBuffer[positionInBuffer] = byteToWrite;
        positionInBuffer++;
    }

    private CreateMultipartUploadResponse sendCreateMultipartUploadRequest() {
        CreateMultipartUploadRequest uploadRequest = CreateMultipartUploadRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        return s3Client.createMultipartUpload(uploadRequest);
    }

    protected void uploadPart() {
        UploadPartResponse uploadPartResponse = sendUploadPartRequest();
        partETags.add(uploadPartResponse.eTag());
        positionInBuffer = 0;
    }

    private UploadPartResponse sendUploadPartRequest() {
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
        writeAllBytes(bytesToWrite, offset, length);
    }

    private void writeAllBytes(byte[] bytesToWrite, int offset, int length) {
        int currentOffset = offset;
        int numOfBytesLeft = length;
        while (willOverflowBuffer(numOfBytesLeft)) {
            int numOfBytesInBuffer = getNumOfBytesInBuffer();
            copyChunkToBuffer(bytesToWrite, currentOffset, numOfBytesInBuffer);
            flushBuffer();
            currentOffset += numOfBytesInBuffer;
            numOfBytesLeft -= numOfBytesInBuffer;
        }
        copyChunkToBuffer(bytesToWrite, currentOffset, numOfBytesLeft);
    }

    private boolean willOverflowBuffer(int numOfBytesLeft) {
        return numOfBytesLeft > getNumOfBytesInBuffer();
    }

    private int getNumOfBytesInBuffer() {
        return temporaryBuffer.length - positionInBuffer;
    }

    private void copyChunkToBuffer(byte[] bytesToWrite, int currentOffset, int numOfBytesToWrite) {
        System.arraycopy(bytesToWrite, currentOffset, temporaryBuffer, positionInBuffer, numOfBytesToWrite);
        positionInBuffer += numOfBytesToWrite;
    }

    /**
     * Flushes the buffer by uploading a part to S3.
     * <p>
     * Forces a multi-part upload.
     */
    @Override
    public synchronized void flush() {
        assertOpen();
        flushBuffer();
    }

    @Override
    public void close() {
        if (!isOpen) {
            return;
        }
        isOpen = false;

        if (isMultiPartUpload()) {
            completeMultipartUpload();
        } else {
            sendPutObjectRequest();
        }
    }

    private boolean hasDataInBuffer() {
        return positionInBuffer > 0;
    }

    private void completeMultipartUpload() {
        if (hasDataInBuffer()) {
            uploadPart();
        }
        sendCompleteMultipartUploadRequest();
    }

    private void sendCompleteMultipartUploadRequest() {
        CompletedPart[] completedParts = getCompletedParts();

        s3Client.completeMultipartUpload(request -> request
                .bucket(bucket)
                .key(key)
                .uploadId(uploadId)
                .multipartUpload(multipartUpload -> multipartUpload.parts(completedParts)));
    }

    private CompletedPart[] getCompletedParts() {
        var completedParts = new CompletedPart[partETags.size()];
        for (int i = 0; i < partETags.size(); i++) {
            completedParts[i] = buildCompletedPart(i);
        }
        return completedParts;
    }

    private CompletedPart buildCompletedPart(int i) {
        return CompletedPart.builder()
                .eTag(partETags.get(i))
                .partNumber(i + 1)
                .build();
    }

    private void sendPutObjectRequest() {
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentLength((long) positionInBuffer)
                .build();
        RequestBody requestBody = createRequestBody();

        s3Client.putObject(putRequest, requestBody);
    }
}
