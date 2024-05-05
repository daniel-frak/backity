package dev.codesoapbox.backity.core.filedetails.application;

import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetailsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static dev.codesoapbox.backity.core.filedetails.domain.TestFileDetails.inProgressFileDetails;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCurrentlyDownloadingFileUseCaseTest {

    private GetCurrentlyDownloadingFileUseCase useCase;

    @Mock
    private FileDetailsRepository fileDetailsRepository;

    @BeforeEach
    void setUp() {
        useCase = new GetCurrentlyDownloadingFileUseCase(fileDetailsRepository);
    }

    @Test
    void shouldFindCurrentlyDownloadingFile() {
        Optional<FileDetails> fileDetails = Optional.of(inProgressFileDetails().build());
        when(fileDetailsRepository.findCurrentlyDownloading())
                .thenReturn(fileDetails);

        Optional<FileDetails> result = useCase.findCurrentlyDownloadingFile();

        assertThat(result).isEqualTo(fileDetails);
    }
}