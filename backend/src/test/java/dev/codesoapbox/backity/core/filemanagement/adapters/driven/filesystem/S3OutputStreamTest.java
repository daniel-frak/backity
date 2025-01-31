package dev.codesoapbox.backity.core.filemanagement.adapters.driven.filesystem;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class S3OutputStreamTest {

    private static final String BUCKET_NAME = "test-bucket";
    private static final String OBJECT_KEY = "test-key";
    private static final String MULTI_PART_UPLOAD_ID = "test-upload-id";
    private static final String UPLOAD_PART_E_TAG = "eTag1";
    private static final int LETTER_A_ASCII = 65;
    private static final int BUFFER_SIZE_BYTES = 5;

    private S3Client s3Client;
    private S3OutputStream s3OutputStream;

    @BeforeEach
    void setUp() {
        s3Client = mock(S3Client.class);
        s3OutputStream = new S3OutputStream(s3Client, BUCKET_NAME, OBJECT_KEY, BUFFER_SIZE_BYTES);
    }

    @Test
    void writeSingleByteShouldThrowGivenStreamIsClosed() {
        s3OutputStream.close();

        assertThatThrownBy(() -> s3OutputStream.write(LETTER_A_ASCII))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Stream closed");
    }

    @Test
    void writeArrayOfBytesShouldThrowGivenStreamIsClosed() {
        byte[] data = "hello".getBytes();
        s3OutputStream.close();

        assertThatThrownBy(() -> s3OutputStream.write(data))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Stream closed");
    }

    @Test
    void shouldWriteSingleByteToBufferGivenBufferIsNotFull() {
        int data = LETTER_A_ASCII;

        s3OutputStream.write(data);

        assertThat(s3OutputStream.temporaryBuffer).startsWith(data);
        verifyNoInteractions(s3Client);
    }

    @Test
    void shouldWriteByteArrayToBufferGivenBufferIsNotFull() {
        byte[] data = "hello".getBytes();

        s3OutputStream.write(data);

        assertThat(s3OutputStream.temporaryBuffer).startsWith(data);
        verifyNoInteractions(s3Client);
    }

    @Test
    void writeSingleByteShouldFlushBufferGivenItIsFull() {
        mockMultipartUploadCreationAndPartUpload();
        byte[] fullBufferData = new byte[BUFFER_SIZE_BYTES];

        s3OutputStream.write(fullBufferData);
        s3OutputStream.write(LETTER_A_ASCII);

        assertThatMultipartUploadWasCreated();
        assertPartWasUploaded(fullBufferData, 1);
        assertNumberOfPartsUploaded(1);
        assertThat(s3OutputStream.temporaryBuffer).startsWith(LETTER_A_ASCII);
        verifyNoMoreInteractions(s3Client);
    }

    private void assertThatMultipartUploadWasCreated() {
        ArgumentCaptor<CreateMultipartUploadRequest> uploadRequestCaptor =
                ArgumentCaptor.forClass(CreateMultipartUploadRequest.class);
        verify(s3Client).createMultipartUpload(uploadRequestCaptor.capture());

        CreateMultipartUploadRequest capturedRequest = uploadRequestCaptor.getValue();
        assertThat(capturedRequest.bucket()).isEqualTo(BUCKET_NAME);
        assertThat(capturedRequest.key()).isEqualTo(OBJECT_KEY);
    }

    @SneakyThrows
    private void assertPartWasUploaded(byte[] content, int partNumber) {
        ArgumentCaptor<UploadPartRequest> uploadPartRequestCaptor =
                ArgumentCaptor.forClass(UploadPartRequest.class);
        ArgumentCaptor<RequestBody> requestBodyCaptor = ArgumentCaptor.forClass(RequestBody.class);
        verify(s3Client, atLeastOnce()).uploadPart(uploadPartRequestCaptor.capture(), requestBodyCaptor.capture());

        UploadPartRequest capturedRequest = uploadPartRequestCaptor.getAllValues().stream()
                .filter(request -> request.partNumber() == partNumber)
                .findFirst()
                .orElseGet(() -> fail("Could not find upload part " + partNumber));

        assertThat(capturedRequest.bucket()).isEqualTo(BUCKET_NAME);
        assertThat(capturedRequest.key()).isEqualTo(OBJECT_KEY);
        assertThat(capturedRequest.uploadId()).isEqualTo(MULTI_PART_UPLOAD_ID);
        assertThat(capturedRequest.contentLength()).isEqualTo(content.length);

        RequestBody capturedRequestBody = requestBodyCaptor.getAllValues().get(partNumber - 1);
        try (InputStream inputStream = capturedRequestBody.contentStreamProvider().newStream()) {
            byte[] sentBytes = inputStream.readAllBytes();
            assertThat(sentBytes).isEqualTo(content);
        }
    }

    private void mockMultipartUploadCreationAndPartUpload() {
        mockMultipartUploadCreation();
        mockPartUpload();
    }

    private void mockMultipartUploadCreation() {
        CreateMultipartUploadResponse response = CreateMultipartUploadResponse.builder()
                .uploadId(MULTI_PART_UPLOAD_ID)
                .build();
        when(s3Client.createMultipartUpload(any(CreateMultipartUploadRequest.class)))
                .thenReturn(response);
    }

    private void mockPartUpload() {
        UploadPartResponse uploadPartResponse = mock(UploadPartResponse.class);
        AtomicInteger partNumber = new AtomicInteger(1);
        when(uploadPartResponse.eTag())
                .thenAnswer(inv -> UPLOAD_PART_E_TAG + "_" + partNumber.getAndIncrement());
        when(s3Client.uploadPart(any(UploadPartRequest.class), any(RequestBody.class)))
                .thenReturn(uploadPartResponse);
    }

    private void assertNumberOfPartsUploaded(int times) {
        verify(s3Client, times(times)).uploadPart(any(UploadPartRequest.class), any(RequestBody.class));
    }

    @Test
    void writeByteArrayShouldFlushBufferGivenItIsFull() {
        mockMultipartUploadCreationAndPartUpload();
        byte[] fullBufferData = new byte[BUFFER_SIZE_BYTES];
        byte[] bufferOverflowingData = new byte[BUFFER_SIZE_BYTES + 1];
        bufferOverflowingData[BUFFER_SIZE_BYTES] = LETTER_A_ASCII;

        s3OutputStream.write(bufferOverflowingData);

        assertThatMultipartUploadWasCreated();
        assertPartWasUploaded(fullBufferData, 1);
        assertNumberOfPartsUploaded(1);
        assertThat(s3OutputStream.temporaryBuffer).startsWith(LETTER_A_ASCII);
        verifyNoMoreInteractions(s3Client);
    }

    @Test
    void shouldWriteByteArrayWithOffsetAndLengthToBuffer() {
        byte[] data = "abcdefghij".getBytes();

        s3OutputStream.write(data, 2, BUFFER_SIZE_BYTES);

        assertThat(s3OutputStream.temporaryBuffer).startsWith("cdefg".getBytes());
        verifyNoInteractions(s3Client);
    }

    @Test
    void writeShouldNotFlushGivenBufferSizeNotExceeded() {
        byte[] fullBufferData = new byte[BUFFER_SIZE_BYTES];

        s3OutputStream.write(fullBufferData);

        verifyNoInteractions(s3Client);
    }

    @Test
    void writeShouldFlushBufferAndUploadPartsWithCorrectNumbersWhenFull() {
        mockMultipartUploadCreationAndPartUpload();
        var fullBufferData = new byte[BUFFER_SIZE_BYTES];
        s3OutputStream.write(fullBufferData); // Triggers multipart upload creation and first part upload
        s3OutputStream.write(fullBufferData); // Triggers second part upload
        s3OutputStream.write(fullBufferData); // Triggers flush, but doesn't upload third part because it fits in buffer

        assertThatMultipartUploadWasCreated();
        assertPartWasUploaded(fullBufferData, 1);
        assertPartWasUploaded(fullBufferData, 2);
        assertNumberOfPartsUploaded(2);
        verifyNoMoreInteractions(s3Client);
    }

    @Test
    void flushShouldThrowGivenStreamIsClosed() {
        s3OutputStream.close();

        assertThatThrownBy(() -> s3OutputStream.flush())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Stream closed");
    }

    @Test
    void flushShouldStartMultipartUploadAndUploadPartGivenBufferNotEmpty() {
        mockMultipartUploadCreationAndPartUpload();

        s3OutputStream.write(LETTER_A_ASCII);
        s3OutputStream.flush();

        assertThatMultipartUploadWasCreated();
        assertPartWasUploaded(new byte[]{LETTER_A_ASCII}, 1);
        assertNumberOfPartsUploaded(1);
        verifyNoMoreInteractions(s3Client);
    }

    @Test
    void shouldThrowExceptionWhenWritingToClosedStream() {
        s3OutputStream.close();

        assertThatWritingToStreamCausesException();
    }

    private void assertThatWritingToStreamCausesException() {
        assertThatThrownBy(() -> s3OutputStream.write(LETTER_A_ASCII))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Stream closed");
    }

    @Test
    void shouldCloseStreamOnCancel() {
        s3OutputStream.cancel();

        assertThatWritingToStreamCausesException();
        verifyNoInteractions(s3Client);
    }

    @Test
    void shouldAbortUploadOnCancelGivenMultipartUpload() {
        mockMultipartUploadCreationAndPartUpload();
        // When the upload doesn't fit within the buffer, it becomes a multipart upload:
        triggerFlushInS3OutputStreamByOverfillingBuffer();

        s3OutputStream.cancel();

        assertMultiPartUploadWasAborted();
    }

    private void triggerFlushInS3OutputStreamByOverfillingBuffer() {
        var fullBufferData = new byte[BUFFER_SIZE_BYTES];
        s3OutputStream.write(fullBufferData);
        s3OutputStream.write(LETTER_A_ASCII);
    }

    private void assertMultiPartUploadWasAborted() {
        ArgumentCaptor<AbortMultipartUploadRequest> abortCaptor =
                ArgumentCaptor.forClass(AbortMultipartUploadRequest.class);
        verify(s3Client).abortMultipartUpload(abortCaptor.capture());

        AbortMultipartUploadRequest capturedRequest = abortCaptor.getValue();
        assertThat(capturedRequest.bucket()).isEqualTo(BUCKET_NAME);
        assertThat(capturedRequest.key()).isEqualTo(OBJECT_KEY);
        assertThat(capturedRequest.uploadId()).isEqualTo(MULTI_PART_UPLOAD_ID);
    }

    @Test
    void closeShouldOnlyCompleteUploadGivenMultipartUploadWithNoBufferedData() {
        mockMultipartUploadWithNoBufferedData();
        CompleteMultipartUploadRequest.Builder completeMultipartUploadRequestBuilder =
                interceptCompleteMultipartUploadRequestBuilder();

        s3OutputStream.close();

        assertMultipartUploadWasCompletedWithNumberOfParts(
                completeMultipartUploadRequestBuilder, 1);
        verifyNoMoreInteractions(s3Client);
    }

    private void mockMultipartUploadWithNoBufferedData() {
        mockMultipartUploadCreationAndPartUpload();
        var fullBufferData = new byte[BUFFER_SIZE_BYTES];
        s3OutputStream.write(fullBufferData);
        s3OutputStream.flush(); // Uploads the buffered data so the buffer becomes empty and forces a multipart upload

        // Asserting here for cleaner `verifyNoMoreInteractions(s3Client);` later
        assertThatMultipartUploadWasCreated();
        assertPartWasUploaded(fullBufferData, 1);
        assertNumberOfPartsUploaded(1);
    }

    @SuppressWarnings("unchecked")
    private CompleteMultipartUploadRequest.Builder interceptCompleteMultipartUploadRequestBuilder() {
        CompleteMultipartUploadRequest.Builder completeMultipartUploadRequestBuilder =
                CompleteMultipartUploadRequest.builder();
        when(s3Client.completeMultipartUpload(any(Consumer.class)))
                .thenAnswer(inv -> {
                    ((Consumer<CompleteMultipartUploadRequest.Builder>) inv.getArgument(0))
                            .accept(completeMultipartUploadRequestBuilder);
                    return null;
                });

        return completeMultipartUploadRequestBuilder;
    }

    @SuppressWarnings("unchecked")
    private void assertMultipartUploadWasCompletedWithNumberOfParts(
            CompleteMultipartUploadRequest.Builder requestBuilder,
            int numberOfUploadedPartsExpected) {
        verify(s3Client).completeMultipartUpload(any(Consumer.class));

        CompleteMultipartUploadRequest capturedRequest = requestBuilder.build();
        assertThat(capturedRequest.bucket()).isEqualTo(BUCKET_NAME);
        assertThat(capturedRequest.key()).isEqualTo(OBJECT_KEY);
        assertThat(capturedRequest.uploadId()).isEqualTo(MULTI_PART_UPLOAD_ID);
        assertThat(capturedRequest.multipartUpload()).isNotNull();
        assertCorrectPartInformationWasPassed(capturedRequest, numberOfUploadedPartsExpected);
    }

    private void assertCorrectPartInformationWasPassed(CompleteMultipartUploadRequest capturedRequest,
                                                       int numberOfUploadedPartsExpected) {
        List<CompletedPart> parts = capturedRequest.multipartUpload().parts();
        assertThat(parts).hasSize(numberOfUploadedPartsExpected);
        assertPartsWereCorrectlyAssembled(parts);
    }

    private void assertPartsWereCorrectlyAssembled(List<CompletedPart> parts) {
        List<String> actualETags = new ArrayList<>();
        List<Integer> actualPartNumbers = new ArrayList<>();
        List<String> expectedETags = new ArrayList<>();
        List<Integer> expectedPartNumbers = new ArrayList<>();
        for (int i = 0; i < parts.size(); i++) {
            CompletedPart completedPart = parts.get(i);
            actualETags.add(completedPart.eTag());
            actualPartNumbers.add(completedPart.partNumber());
            expectedETags.add(UPLOAD_PART_E_TAG + "_" + (i + 1));
            expectedPartNumbers.add(i + 1);
        }
        assertThat(actualETags).containsExactlyElementsOf(expectedETags);
        assertThat(actualPartNumbers).containsExactlyElementsOf(expectedPartNumbers);
    }

    @Test
    void closeShouldUploadBufferedPartAndCompleteUploadGivenMultipartUploadWithSomeBufferedData() {
        mockMultiPartUploadWithSomeBufferedData();
        CompleteMultipartUploadRequest.Builder completeMultipartUploadRequestBuilder =
                interceptCompleteMultipartUploadRequestBuilder();

        s3OutputStream.close();

        assertPartWasUploaded(new byte[]{LETTER_A_ASCII}, 2);
        assertNumberOfPartsUploaded(2);
        assertMultipartUploadWasCompletedWithNumberOfParts(
                completeMultipartUploadRequestBuilder, 2);
        verifyNoMoreInteractions(s3Client);
    }

    private void mockMultiPartUploadWithSomeBufferedData() {
        mockMultipartUploadCreationAndPartUpload();
        var fullBufferData = new byte[BUFFER_SIZE_BYTES];
        s3OutputStream.write(fullBufferData); // The first flush sets uploadId, making it a multipart upload
        s3OutputStream.write(LETTER_A_ASCII);
        assertThatMultipartUploadWasCreated(); // Asserting here for cleaner `verifyNoMoreInteractions(s3Client);` later
    }

    @Test
    void closeShouldFallbackToPutObjectGivenSinglePartUpload() {
        byte data = LETTER_A_ASCII;
        s3OutputStream.write(data);

        s3OutputStream.close();

        assertThatPutObjectWasUsedToUpload(new byte[]{data});
        verifyNoMoreInteractions(s3Client);
    }

    private void assertThatPutObjectWasUsedToUpload(byte[] data) {
        ArgumentCaptor<PutObjectRequest> putObjectCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(putObjectCaptor.capture(), any(RequestBody.class));

        PutObjectRequest capturedRequest = putObjectCaptor.getValue();
        assertThat(capturedRequest.bucket()).isEqualTo(BUCKET_NAME);
        assertThat(capturedRequest.key()).isEqualTo(OBJECT_KEY);
        assertThat(capturedRequest.contentLength()).isEqualTo(data.length);
    }

    @Test
    void closeShouldDoNothingGivenStreamIsAlreadyClosed() {
        writeDataAndCloseStream();

        s3OutputStream.close();

        verify(s3Client, times(1))
                .putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verifyNoMoreInteractions(s3Client);
    }

    private void writeDataAndCloseStream() {
        s3OutputStream.write((byte) LETTER_A_ASCII);
        s3OutputStream.close();
    }
}
