package dev.codesoapbox.backity.core.files.application;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.model.TestGameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileDetailsRepository;
import dev.codesoapbox.backity.core.files.domain.game.Game;
import dev.codesoapbox.backity.core.files.domain.game.GameRepository;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PageJson;
import dev.codesoapbox.backity.core.shared.domain.Page;
import dev.codesoapbox.backity.core.shared.domain.Pagination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameFacadeTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameFileDetailsRepository gameFileRepository;

    private GameFacade gameFacade;

    @BeforeEach
    void setUp() {
        gameFacade = new GameFacade(gameRepository, gameFileRepository);
    }

    @Test
    void shouldGetGamesWithFiles() {
        var pageable = new Pagination(0, 2);
        Game game = Game.createNew("Test game");
        var gameFile = TestGameFileDetails.discovered().build();
        List<GameFileDetails> gameFileDetails = singletonList(gameFile);

        when(gameRepository.findAll(pageable))
                .thenReturn(new Page<>(singletonList(game), 1, 2, 3, 4, 5));
        when(gameFileRepository.findAllByGameId(game.getId()))
                .thenReturn(gameFileDetails);

        Page<GameWithFiles> result = gameFacade.getGamesWithFiles(pageable);

        PageJson<GameWithFiles> expectedResult = new PageJson<>(singletonList(new GameWithFiles(game, gameFileDetails)),
                1, 2, 3, 4, 5);
        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }
}