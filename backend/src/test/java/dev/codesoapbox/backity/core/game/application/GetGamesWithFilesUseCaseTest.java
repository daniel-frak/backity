package dev.codesoapbox.backity.core.game.application;

import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import dev.codesoapbox.backity.shared.domain.TestPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetGamesWithFilesUseCaseTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameFileRepository gameFileRepository;

    private GetGamesWithFilesUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetGamesWithFilesUseCase(gameRepository, gameFileRepository);
    }

    @Test
    void shouldGetGamesWithFiles() {
        var pagination = new Pagination(0, 2);
        Game game = mockGameExists(pagination);
        List<GameFile> gameFiles = mockGameFilesExistFor(game);

        Page<GameWithFiles> result = useCase.getGamesWithFiles(pagination);

        Page<GameWithFiles> expectedResult = TestPage.of(List.of(new GameWithFiles(game, gameFiles)), pagination);
        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }

    private Game mockGameExists(Pagination pagination) {
        Game game = Game.createNew("Test game");
        when(gameRepository.findAll(pagination))
                .thenReturn(TestPage.of(List.of(game), pagination));

        return game;
    }

    private List<GameFile> mockGameFilesExistFor(Game game) {
        List<GameFile> gameFiles = List.of(TestGameFile.discovered());
        when(gameFileRepository.findAllByGameId(game.getId()))
                .thenReturn(gameFiles);

        return gameFiles;
    }
}