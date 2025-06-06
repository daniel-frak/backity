package dev.codesoapbox.backity.core.game.application.usecases;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgress;
import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.game.application.FileCopyWithProgress;
import dev.codesoapbox.backity.core.game.application.GameFileWithCopies;
import dev.codesoapbox.backity.core.game.application.GameWithFileCopies;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@RequiredArgsConstructor
public class GetGamesWithFilesUseCase {

    private final GameRepository gameRepository;
    private final GameFileRepository gameFileRepository;
    private final FileCopyRepository fileCopyRepository;
    private final FileCopyReplicationProgressRepository replicationProgressRepository;

    public Page<GameWithFileCopies> getGamesWithFiles(Pagination pagination) {
        Page<Game> games = gameRepository.findAll(pagination);
        return games.map(this::findFilesWithCopies);
    }

    private GameWithFileCopies findFilesWithCopies(Game game) {
        List<GameFile> gameFiles = gameFileRepository.findAllByGameId(game.getId());
        List<GameFileWithCopies> gameFilesWithCopies = gameFiles.stream()
                .map(this::addFileCopies)
                .toList();
        return new GameWithFileCopies(game, gameFilesWithCopies);
    }

    private GameFileWithCopies addFileCopies(GameFile gameFile) {
        List<FileCopy> fileCopies = fileCopyRepository.findAllByGameFileId(gameFile.getId());
        List<FileCopyWithProgress> fileCopiesWithProgress = addProgresses(fileCopies);

        return new GameFileWithCopies(gameFile, fileCopiesWithProgress);
    }

    private List<FileCopyWithProgress> addProgresses(List<FileCopy> fileCopies) {
        Map<FileCopyId, FileCopyReplicationProgress> progressesByFileCopyId =
                getReplicationProgressesByFileCopyId(fileCopies);

        return fileCopies.stream()
                .map(fileCopy -> toFileCopyWithProgress(fileCopy, progressesByFileCopyId))
                .toList();
    }

    private FileCopyWithProgress toFileCopyWithProgress(
            FileCopy fileCopy, Map<FileCopyId, FileCopyReplicationProgress> progressesByFileCopyId) {
        FileCopyReplicationProgress progress = progressesByFileCopyId.get(fileCopy.getId());
        return new FileCopyWithProgress(fileCopy, progress);
    }

    private Map<FileCopyId, FileCopyReplicationProgress> getReplicationProgressesByFileCopyId(
            List<FileCopy> fileCopies) {
        Set<FileCopyId> fileCopyIds = fileCopies.stream()
                .map(FileCopy::getId)
                .collect(toSet());

        return replicationProgressRepository.findAllByFileCopyIdIn(fileCopyIds).stream()
                .collect(toMap(FileCopyReplicationProgress::fileCopyId, identity()));
    }
}
