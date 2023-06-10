package dev.codesoapbox.backity.core.files.application;

import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileDetailsRepository;
import dev.codesoapbox.backity.core.files.domain.game.Game;
import dev.codesoapbox.backity.core.files.domain.game.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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
        var pageable = PageRequest.of(0, 2);
        Game game = Game.createNew("Test game");
        var gameFile = new GameFileDetails(
                1L, "someSource", "someUrl", "someTitle", "someOriginalFileName",
                null, null, "someGameId", "someVersion", "100 KB",
                null, null, FileBackupStatus.DISCOVERED, null);
        List<GameFileDetails> gameFileDetails = singletonList(gameFile);

        when(gameRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(singletonList(game)));
        when(gameFileRepository.findAllByGameId(game.getId()))
                .thenReturn(gameFileDetails);

        Page<GameWithFiles> result = gameFacade.getGamesWithFiles(pageable);

        Page<GameWithFiles> expectedResult = new PageImpl<>(singletonList(new GameWithFiles(game, gameFileDetails)));
        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }
}