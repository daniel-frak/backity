package dev.codesoapbox.backity.core.filemanagement.config;

import dev.codesoapbox.backity.core.filemanagement.adapters.driven.filesystem.S3FileSystem;
import dev.codesoapbox.backity.core.filemanagement.domain.FileManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.S3Client;

@ConditionalOnProperty(value = "backity.filesystem.s3.enabled", havingValue = "true")
@Configuration
public class FileSystemBeanConfig {

    @Bean
    FileManager s3FileSystem(S3Client s3Client, S3Properties s3Properties) {
        return new S3FileSystem(s3Client, s3Properties.bucket(), s3Properties.bufferSizeInBytes());
    }

    @ConfigurationProperties("backity.filesystem.s3")
    public record S3Properties(
            String enabled,
            String bucket,
            @Value("${buffer-size-in-bytes}") int bufferSizeInBytes
    ) {
    }
}
