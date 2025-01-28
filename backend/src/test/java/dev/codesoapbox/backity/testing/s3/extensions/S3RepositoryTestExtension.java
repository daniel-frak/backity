package dev.codesoapbox.backity.testing.s3.extensions;

import dev.codesoapbox.backity.BackityApplication;
import dev.codesoapbox.backity.testing.s3.annotations.S3RepositoryTest;
import dev.codesoapbox.backity.testing.s3.containers.LocalStackContainerInitializer;
import dev.codesoapbox.backity.testing.s3.containers.LocalStackContainerWrapper;
import org.junit.jupiter.api.extension.*;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * JUnit Jupiter {@code @Extension} for managing an S3 test environment using LocalStack.
 * It handles the setup and teardown of S3 buckets and objects for test classes annotated with {@link S3RepositoryTest}.
 * It also provides the ability to inject an {@link S3Client} into test methods or classes.
 */
public class S3RepositoryTestExtension
        implements BeforeAllCallback, BeforeEachCallback, AfterEachCallback, ParameterResolver {

    public static final LocalStackContainerWrapper LOCAL_STACK_CONTAINER =
            LocalStackContainerInitializer.getContainer();
    private S3Client s3Client;
    private List<String> buckets;
    private List<S3RepositoryTest.FileToUpload> filesToUpload;

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return S3Client.class.equals(parameterContext.getParameter().getType());
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return s3Client;
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        LOCAL_STACK_CONTAINER.start();

        s3Client = LOCAL_STACK_CONTAINER.buildS3Client();

        if (extensionContext.getElement().isPresent() &&
            extensionContext.getElement().get().isAnnotationPresent(S3RepositoryTest.class)) {
            buckets = List.of(extensionContext.getElement().get().getAnnotation(S3RepositoryTest.class).buckets());
            filesToUpload =
                    List.of(extensionContext.getElement().get().getAnnotation(S3RepositoryTest.class).filesToUpload());
        }
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        for (String bucket : getBuckets()) {
            s3Client.createBucket(request -> request.bucket(bucket));
        }
        for (S3RepositoryTest.FileToUpload fileToUpload : filesToUpload) {
            if (!fileToUpload.filePath().isBlank() && !fileToUpload.fileContent().isBlank()) {
                throw new IllegalArgumentException(
                        "Both file path and file content cannot be passed at the same time for: " + fileToUpload.key());
            }
            if (!fileToUpload.filePath().isBlank()) {
                uploadFileFromFileSystem(fileToUpload.filePath(), fileToUpload.bucket(), fileToUpload.key());
            } else if (!fileToUpload.fileContent().isBlank()) {
                uploadFileFromMemory(fileToUpload.fileContent(), fileToUpload.bucket(), fileToUpload.key());
            } else {
                throw new IllegalArgumentException("Must provide either file path or file content for: "
                                                   + fileToUpload.key());
            }
        }
    }

    private List<String> getBuckets() {
        Stream<String> fileBuckets = filesToUpload.stream()
                .map(S3RepositoryTest.FileToUpload::bucket);
        return Stream.concat(buckets.stream(), fileBuckets)
                .distinct()
                .toList();
    }

    private void uploadFileFromFileSystem(String filePath, String bucket, String key) {
        File file = getFile(filePath);
        s3Client.putObject(request -> request.bucket(bucket).key(key), RequestBody.fromFile(file));
    }

    private File getFile(String filePath) {
        URL resourceURL = BackityApplication.class.getClassLoader().getResource(filePath);
        return new File(Objects.requireNonNull(resourceURL).getFile());
    }

    private void uploadFileFromMemory(String fileContent, String bucket, String key) {
        s3Client.putObject(request -> request.bucket(bucket).key(key), RequestBody.fromString(fileContent));
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        for (String bucket : getBuckets()) {
            deleteBucket(bucket);
        }
    }

    private void deleteBucket(String bucket) {
        deleteBucketContents(bucket);
        s3Client.deleteBucket(request -> request.bucket(bucket));
    }

    private void deleteBucketContents(String bucket) {
        ListObjectsResponse objects = s3Client.listObjects(request -> request.bucket(bucket));
        for (S3Object s3Object : objects.contents()) {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3Object.key())
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        }
    }
}
