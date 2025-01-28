package dev.codesoapbox.backity.testing.s3.containers;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * A wrapper for the {@link LocalStackContainer} to provide additional configuration for integration tests.
 * <p>
 * It simplifies the setup of a LocalStack container by predefining the image version
 * and providing utilities for configuring the Spring application context with required AWS properties,
 * as well as creating pre-configured clients.
 * </p>
 */
public class LocalStackContainerWrapper extends LocalStackContainer {

    private static final String IMAGE_VERSION = "localstack/localstack:4.0.3";
    private static final String DEFAULT_IMAGE_NAME = "localstack/localstack";
    private static final String AWS_REGION_PROPERTY = "spring.cloud.aws.region.static";
    private static final String S3_ENDPOINT_PROPERTY = "spring.cloud.aws.s3.endpoint";
    private static final String ACCESS_KEY_PROPERTY = "spring.cloud.aws.credentials.access-key";
    private static final String SECRET_KEY_PROPERTY = "spring.cloud.aws.credentials.secret-key";

    public LocalStackContainerWrapper(DockerImageName dockerImageName) {
        super(dockerImageName);
    }

    public static LocalStackContainerWrapper get() {
        DockerImageName imageName = DockerImageName.parse(IMAGE_VERSION)
                .asCompatibleSubstituteFor(DEFAULT_IMAGE_NAME);
        return new LocalStackContainerWrapper(imageName);
    }

    /**
     * Configures Spring Boot connection with the container
     */
    public void setProperties(ConfigurableApplicationContext applicationContext) {
        TestPropertyValues.of(
                AWS_REGION_PROPERTY + "=" + getRegion(),
                S3_ENDPOINT_PROPERTY + "=" + getEndpointOverride(LocalStackContainer.Service.S3),
                ACCESS_KEY_PROPERTY + "=" + getAccessKey(),
                SECRET_KEY_PROPERTY + "=" + getSecretKey()
        ).applyTo(applicationContext.getEnvironment());
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
