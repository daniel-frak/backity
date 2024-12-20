package dev.codesoapbox.backity.core.gamefile.application;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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
        Optional<GameFile> maybeGameFile = Optional.of(TestGameFile.inProgress());
        when(gameFileRepository.findCurrentlyDownloading())
                .thenReturn(maybeGameFile);

        Optional<GameFile> result = useCase.findCurrentlyDownloadingFile();

        assertThat(result).isEqualTo(maybeGameFile);
    }
}