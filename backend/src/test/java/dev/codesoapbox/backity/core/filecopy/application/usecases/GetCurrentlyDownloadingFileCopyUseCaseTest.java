package dev.codesoapbox.backity.core.filecopy.application.usecases;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.game.domain.TestGame;
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
class GetCurrentlyDownloadingFileCopyUseCaseTest {

    private GetCurrentlyDownloadingFileCopyUseCase useCase;

    @Mock
    private FileCopyRepository fileCopyRepository;

    @Mock
    private GameFileRepository gameFileRepository;

    @Mock
    private GameRepository gameRepository;

    @BeforeEach
    void setUp() {
        useCase = new GetCurrentlyDownloadingFileCopyUseCase(fileCopyRepository, gameFileRepository, gameRepository);
    }

    @Test
    void shouldFindCurrentlyDownloadingFileCopy() {
        FileCopy fileCopy = mockFileCopyExists();
        GameFile gameFile = mockGameFileExists(fileCopy);
        Game game = mockGameExists(gameFile);

        Optional<FileCopyWithContext> result = useCase.findCurrentlyDownloadingFileCopy();

        var expectedResult = new FileCopyWithContext(fileCopy, gameFile, game);
        assertThat(result).get().isEqualTo(expectedResult);
    }

    private FileCopy mockFileCopyExists() {
        FileCopy fileCopy = TestFileCopy.inProgress();
        when(fileCopyRepository.findOneInProgress())
                .thenReturn(Optional.of(fileCopy));
        return fileCopy;
    }

    private GameFile mockGameFileExists(FileCopy fileCopy) {
        GameFile gameFile = TestGameFile.gog();
        when(gameFileRepository.getById(fileCopy.getNaturalId().gameFileId()))
                .thenReturn(gameFile);
        return gameFile;
    }

    private Game mockGameExists(GameFile gameFile) {
        Game game = TestGame.any();
        when(gameRepository.getById(gameFile.getGameId()))
                .thenReturn(game);
        return game;
    }
}