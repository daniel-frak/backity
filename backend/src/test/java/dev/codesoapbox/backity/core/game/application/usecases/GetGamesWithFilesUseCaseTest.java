package dev.codesoapbox.backity.core.game.application.usecases;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgress;
import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.core.backup.domain.TestFileCopyReplicationProgress;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.game.application.GameWithFileCopiesAndReplicationProgresses;
import dev.codesoapbox.backity.core.game.application.GameWithFileCopiesReadModelRepository;
import dev.codesoapbox.backity.core.game.application.GameWithFileCopiesSearchFilter;
import dev.codesoapbox.backity.core.game.application.TestGameWithFileCopiesSearchFilter;
import dev.codesoapbox.backity.core.game.application.readmodel.*;
import dev.codesoapbox.backity.core.sourcefile.domain.TestSourceFile;
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

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetGamesWithFilesUseCaseTest {

    @Mock
    private GameWithFileCopiesReadModelRepository gameReadModelRepository;

    @Mock
    private FileCopyReplicationProgressRepository replicationProgressRepository;

    private GetGamesWithFilesUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetGamesWithFilesUseCase(gameReadModelRepository, replicationProgressRepository);
    }

    @Test
    void shouldGetGamesWithFilesWithProgress() {
        var pagination = new Pagination(0, 2);
        var searchQuery = "someSearchQuery";
        GameWithFileCopiesSearchFilter filter = TestGameWithFileCopiesSearchFilter.onlySearchQuery(searchQuery);
        FileCopy localCopy = TestFileCopy.storedIntegrityUnknown();
        GameWithFileCopiesReadModel game = gameWithFileCopiesExists(localCopy, pagination, filter);
        FileCopyReplicationProgress replicationProgress = TestFileCopyReplicationProgress.twentyFivePercent();
        replicationProgressExistsForFileCopy(replicationProgress, localCopy);

        Page<GameWithFileCopiesAndReplicationProgresses> result = useCase.execute(pagination, filter);

        var gameWithProgresses = new GameWithFileCopiesAndReplicationProgresses(game, List.of(replicationProgress));
        Page<GameWithFileCopiesAndReplicationProgresses> expectedResult =
                TestPage.of(List.of(gameWithProgresses), pagination);
        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }

    private GameWithFileCopiesReadModel gameWithFileCopiesExists(FileCopy localCopy, Pagination pagination,
                                                                 GameWithFileCopiesSearchFilter filter) {
        GameWithFileCopiesReadModel game = TestGameWithFileCopiesReadModel.withNoSourceFilesBuilder()
                .withSourceFilesWithCopies(List.of(
                        new SourceFileWithCopiesReadModel(
                                TestSourceFileReadModel.from(TestSourceFile.gog()),
                                List.of(TestFileCopyReadModel.from(localCopy))
                                )
                ))
                .build();
        when(gameReadModelRepository.findAllPaginated(pagination, filter))
                .thenReturn(TestPage.of(List.of(game), pagination));

        return game;
    }

    private void replicationProgressExistsForFileCopy(
            FileCopyReplicationProgress replicationProgress, FileCopy localCopy) {
        when(replicationProgressRepository.findAllByFileCopyIdIn(Set.of(localCopy.getId())))
                .thenReturn(List.of(replicationProgress));
    }

    @Test
    void shouldGetGamesWithFilesWithoutProgress() {
        var pagination = new Pagination(0, 2);
        var searchQuery = "someSearchQuery";
        GameWithFileCopiesSearchFilter filter = TestGameWithFileCopiesSearchFilter.onlySearchQuery(searchQuery);
        FileCopy localCopy = TestFileCopy.storedIntegrityUnknown();
        GameWithFileCopiesReadModel game = gameWithFileCopiesExists(localCopy, pagination, filter);

        Page<GameWithFileCopiesAndReplicationProgresses> result = useCase.execute(pagination, filter);

        var gameWithProgresses = new GameWithFileCopiesAndReplicationProgresses(game, emptyList());
        Page<GameWithFileCopiesAndReplicationProgresses> expectedResult =
                TestPage.of(List.of(gameWithProgresses), pagination);
        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }
}