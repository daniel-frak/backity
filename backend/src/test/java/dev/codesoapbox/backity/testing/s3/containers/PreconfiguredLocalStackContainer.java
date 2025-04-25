package dev.codesoapbox.backity.testing.s3.containers;

import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * A wrapper for the {@link LocalStackContainer} which simplifies its setup by predefining the image version,
 * and provides additional methods for easier interaction with its services.
 */
public class PreconfiguredLocalStackContainer extends LocalStackContainer {

    private static final String IMAGE_VERSION = "localstack/localstack:4.0.3";

    public PreconfiguredLocalStackContainer() {
        super(getImageName());
    }

    private static DockerImageName getImageName() {
        return DockerImageName.parse(IMAGE_VERSION)
                .asCompatibleSubstituteFor("localstack/localstack");
    }

    /**
     * A standalone S3Client, to be used when a Spring Context is not needed.
     */
    public S3Client buildS3Client() {
        return S3Client.builder()
                .endpointOverride(getEndpointOverride(LocalStackContainer.Service.S3))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(
                        getAccessKey(), getSecretKey())))
                .region(Region.of(getRegion()))
                .build();
    }
}
