package dev.codesoapbox.backity.core.gamefiledetails.application;

import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetails;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetailsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static dev.codesoapbox.backity.core.gamefiledetails.domain.TestGameFileDetails.inProgressFileDetails;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCurrentlyDownloadingFileUseCaseTest {

    private GetCurrentlyDownloadingFileUseCase useCase;

    @Mock
    private GameFileDetailsRepository gameFileDetailsRepository;

    @BeforeEach
    void setUp() {
        useCase = new GetCurrentlyDownloadingFileUseCase(gameFileDetailsRepository);
    }

    @Test
    void shouldFindCurrentlyDownloadingFile() {
        Optional<GameFileDetails> gameFileDetails = Optional.of(inProgressFileDetails().build());
        when(gameFileDetailsRepository.findCurrentlyDownloading())
                .thenReturn(gameFileDetails);

        Optional<GameFileDetails> result = useCase.findCurrentlyDownloadingFile();

        assertThat(result).isEqualTo(gameFileDetails);
    }
}