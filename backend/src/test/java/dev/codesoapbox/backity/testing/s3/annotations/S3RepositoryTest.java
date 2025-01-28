package dev.codesoapbox.backity.testing.s3.annotations;

import dev.codesoapbox.backity.testing.s3.containers.LocalStackContainerInitializer;
import dev.codesoapbox.backity.testing.s3.extensions.S3RepositoryTestExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.*;

/**
 * Annotation for an S3 repository test.
 * <p>
 * It configures an S3 test environment, then creates the specified S3 buckets and uploads files
 * using the {@link S3RepositoryTestExtension}.
 * <p>
 * Should a Spring Context be used, it will also be automatically configured to use the S3 test environment.
 * <p>
 * Cleanup is handled automatically between each test.
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * @S3RepositoryTest(
 *     buckets = {"test-bucket-1", "test-bucket-2"},
 *     filesToUpload = {
 *         @S3RepositoryTest.FileToUpload(bucket = "test-bucket-1", key = "file1.txt", fileContent = "Hello, S3!"),
 *         @S3RepositoryTest.FileToUpload(bucket = "test-bucket-2", key = "file2.txt", filePath = "files/file2.txt")
 *     }
 * )
 * public class MyS3Test {
 *     @Test
 *     void testS3Operations(S3Client s3Client) {
 *         // Test logic using the injected S3Client
 *     }
 * }
 * }
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ExtendWith(S3RepositoryTestExtension.class)
@ContextConfiguration(initializers = LocalStackContainerInitializer.class)
public @interface S3RepositoryTest {

    /**
     * Specifies the names of the S3 buckets to be created before each test.
     */
    String[] buckets() default {};

    /**
     * Specifies the files to upload to S3 before each test.
     * Each file includes its target bucket, key, and either its content or file path.
     */
    FileToUpload[] filesToUpload() default {};

    /**
     * Configuration for a file to be uploaded to S3.
     */
    @interface FileToUpload {
        /**
         * The name of the S3 bucket to which the file should be uploaded.
         */
        String bucket();

        /**
         * The key under which the file will be stored in the S3 bucket.
         */
        String key();

        /**
         * The source file path on the local filesystem for the file.
         * Cannot be used together with {@code fileContent}.
         */
        String filePath() default "";


        /**
         * The content of the file to upload, provided as a string.
         * Cannot be used together with {@code filePath}.
         */
        String fileContent() default "";
    }
}
