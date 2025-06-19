package dev.codesoapbox.backity.core.game.application.usecases;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgress;
import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.game.application.GameWithFileCopiesAndReplicationProgresses;
import dev.codesoapbox.backity.core.game.application.GameWithFileCopiesReadModelRepository;
import dev.codesoapbox.backity.core.game.application.readmodel.GameWithFileCopiesReadModel;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@RequiredArgsConstructor
public class GetGamesWithFilesUseCase {

    private final GameWithFileCopiesReadModelRepository gameReadModelRepository;
    private final FileCopyReplicationProgressRepository replicationProgressRepository;

    public Page<GameWithFileCopiesAndReplicationProgresses> getGamesWithFiles(Pagination pagination) {
        Page<GameWithFileCopiesReadModel> games = gameReadModelRepository.findAll(pagination);

        Set<FileCopyId> fileCopyIds = games.content().stream()
                .flatMap(game -> game.gameFilesWithCopies().stream()
                        .flatMap(gameFile -> gameFile.fileCopies().stream()
                                .map(fileCopy -> new FileCopyId(fileCopy.id()))))
                .collect(toSet());
        List<FileCopyReplicationProgress> replicationProgresses =
                replicationProgressRepository.findAllByFileCopyIdIn(fileCopyIds).stream()
                        .toList();

        return games.map(game -> new GameWithFileCopiesAndReplicationProgresses(
                game, replicationProgresses));
    }
}
