package dev.codesoapbox.backity.core.gamefile.application;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static dev.codesoapbox.backity.core.gamefile.domain.TestGameFile.inProgressGameFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCurrentlyDownloadingFileUseCaseTest {

    private GetCurrentlyDownloadingFileUseCase useCase;

    @Mock
    private GameFileRepository gameFileRepository;

    @BeforeEach
    void setUp() {
        useCase = new GetCurrentlyDownloadingFileUseCase(gameFileRepository);
    }

    @Test
    void shouldFindCurrentlyDownloadingFile() {
        Optional<GameFile> gameFile = Optional.of(inProgressGameFile().build());
        when(gameFileRepository.findCurrentlyDownloading())
                .thenReturn(gameFile);

        Optional<GameFile> result = useCase.findCurrentlyDownloadingFile();

        assertThat(result).isEqualTo(gameFile);
    }
}