package dev.codesoapbox.backity.core.filedetails.config;

import dev.codesoapbox.backity.core.filedetails.application.*;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetailsRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileDetailsUseCaseBeanConfig {

    @Bean
    public EnqueueFileUseCase enqueueFileUseCase(FileDetailsRepository fileDetailsRepository) {
        return new EnqueueFileUseCase(fileDetailsRepository);
    }

    @Bean
    public GetCurrentlyDownloadingFileUseCase getCurrentlyDownloadingFileUseCase(
            FileDetailsRepository fileDetailsRepository) {
        return new GetCurrentlyDownloadingFileUseCase(fileDetailsRepository);
    }

    @Bean
    public GetDiscoveredFileListUseCase getDiscoveredFileListUseCase(
            FileDetailsRepository fileDetailsRepository) {
        return new GetDiscoveredFileListUseCase(fileDetailsRepository);
    }

    @Bean
    public GetEnqueuedFileListUseCase getEnqueuedFileListUseCase(
            FileDetailsRepository fileDetailsRepository) {
        return new GetEnqueuedFileListUseCase(fileDetailsRepository);
    }

    @Bean
    public GetProcessedFileListUseCase getProcessedFileListUseCase(
            FileDetailsRepository fileDetailsRepository) {
        return new GetProcessedFileListUseCase(fileDetailsRepository);
    }
}
