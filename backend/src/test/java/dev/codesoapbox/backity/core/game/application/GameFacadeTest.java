package dev.codesoapbox.backity.core.game.application;

import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetails;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetailsRepository;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PageHttpDto;
import dev.codesoapbox.backity.core.shared.domain.Page;
import dev.codesoapbox.backity.core.shared.domain.Pagination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static dev.codesoapbox.backity.core.gamefiledetails.domain.TestGameFileDetails.discoveredFileDetails;
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
        GameFileDetails gameFile = discoveredFileDetails().build();
        List<GameFileDetails> gameFileDetails = singletonList(gameFile);

        when(gameRepository.findAll(pageable))
                .thenReturn(new Page<>(singletonList(game), 1, 2, 3, 4, 5));
        when(gameFileRepository.findAllByGameId(game.getId()))
                .thenReturn(gameFileDetails);

        Page<GameWithFiles> result = gameFacade.getGamesWithFiles(pageable);

        PageHttpDto<GameWithFiles> expectedResult = new PageHttpDto<>(singletonList(new GameWithFiles(game, gameFileDetails)),
                1, 2, 3, 4, 5);
        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }
}