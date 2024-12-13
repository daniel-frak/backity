package dev.codesoapbox.backity.core.game.application;

import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.shared.adapters.driving.api.http.model.PageHttpDto;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static dev.codesoapbox.backity.core.gamefile.domain.TestGameFile.discoveredGameFile;
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
        var pageable = new Pagination(0, 2);
        Game game = Game.createNew("Test game");
        GameFile file = discoveredGameFile().build();

        when(gameRepository.findAll(pageable))
                .thenReturn(new Page<>(List.of(game), 1, 2, 3, 4, 5));
        List<GameFile> gameFiles = List.of(file);
        when(gameFileRepository.findAllByGameId(game.getId()))
                .thenReturn(gameFiles);

        Page<GameWithFiles> result = useCase.getGamesWithFiles(pageable);

        PageHttpDto<GameWithFiles> expectedResult = new PageHttpDto<>(List.of(new GameWithFiles(game, gameFiles)),
                1, 2, 3, 4, 5);
        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }
}