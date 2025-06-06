package dev.codesoapbox.backity.core.game.application.usecases;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgress;
import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.core.backup.domain.TestFileCopyReplicationProgress;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.game.application.FileCopyWithProgress;
import dev.codesoapbox.backity.core.game.application.GameFileWithCopies;
import dev.codesoapbox.backity.core.game.application.GameWithFileCopies;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetGamesWithFilesUseCaseTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameFileRepository gameFileRepository;

    @Mock
    private FileCopyRepository fileCopyRepository;

    @Mock
    private FileCopyReplicationProgressRepository replicationProgressRepository;

    private GetGamesWithFilesUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetGamesWithFilesUseCase(gameRepository, gameFileRepository, fileCopyRepository,
                replicationProgressRepository);
    }

    @Test
    void shouldGetGamesWithFilesWithProgress() {
        var pagination = new Pagination(0, 2);
        GameFile gameFile = TestGameFile.gog();
        FileCopy localCopy = TestFileCopy.storedIntegrityUnknown();
        Game game = mockGameExists(pagination);
        mockGameFilesExistFor(game, List.of(gameFile));
        mockFileCopiesExist(gameFile, List.of(localCopy));
        FileCopyReplicationProgress replicationProgress = mockReplicationProgressExists(localCopy);

        Page<GameWithFileCopies> result = useCase.getGamesWithFiles(pagination);

        var gameWithFiles = new GameWithFileCopies(game,
                List.of(new GameFileWithCopies(gameFile,
                        List.of(new FileCopyWithProgress(localCopy, replicationProgress)))));
        Page<GameWithFileCopies> expectedResult = TestPage.of(List.of(gameWithFiles), pagination);
        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }

    private void mockFileCopiesExist(GameFile gameFile, List<FileCopy> fileCopies) {
        when(fileCopyRepository.findAllByGameFileId(gameFile.getId()))
                .thenReturn(fileCopies);
    }

    private Game mockGameExists(Pagination pagination) {
        Game game = Game.createNew("Test game");
        when(gameRepository.findAll(pagination))
                .thenReturn(TestPage.of(List.of(game), pagination));

        return game;
    }

    private void mockGameFilesExistFor(Game game, List<GameFile> gameFiles) {
        when(gameFileRepository.findAllByGameId(game.getId()))
                .thenReturn(gameFiles);
    }

    private FileCopyReplicationProgress mockReplicationProgressExists(FileCopy localCopy) {
        FileCopyReplicationProgress replicationProgress = TestFileCopyReplicationProgress.twentyFivePercent();
        when(replicationProgressRepository.findAllByFileCopyIdIn(Set.of(localCopy.getId())))
                .thenReturn(List.of(replicationProgress));
        return replicationProgress;
    }

    @Test
    void shouldGetGamesWithFilesWithoutProgress() {
        var pagination = new Pagination(0, 2);
        GameFile gameFile = TestGameFile.gog();
        FileCopy localCopy = TestFileCopy.storedIntegrityUnknown();
        Game game = mockGameExists(pagination);
        mockGameFilesExistFor(game, List.of(gameFile));
        mockFileCopiesExist(gameFile, List.of(localCopy));

        Page<GameWithFileCopies> result = useCase.getGamesWithFiles(pagination);

        var gameWithFiles = new GameWithFileCopies(game,
                List.of(new GameFileWithCopies(gameFile,
                        List.of(new FileCopyWithProgress(localCopy, null)))));
        Page<GameWithFileCopies> expectedResult = TestPage.of(List.of(gameWithFiles), pagination);
        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }
}