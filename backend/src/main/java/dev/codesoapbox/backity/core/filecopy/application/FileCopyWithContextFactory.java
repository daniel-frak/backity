package dev.codesoapbox.backity.core.filecopy.application;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.filecopy.application.usecases.FileCopyWithContext;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.shared.domain.Page;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Set;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@SuppressWarnings(
        // These maps will be small, so lack of Comparable is not a big concern
        "java:S6411")
@RequiredArgsConstructor
public class FileCopyWithContextFactory {

    private final GameFileRepository gameFileRepository;
    private final GameRepository gameRepository;
    private final BackupTargetRepository backupTargetRepository;

    public Page<FileCopyWithContext> createPageFrom(Page<FileCopy> fileCopyPage) {
        Map<GameFileId, GameFile> gameFilesById = getGameFilesById(fileCopyPage);
        Map<GameId, Game> gamesById = getGamesById(gameFilesById);
        Map<BackupTargetId, BackupTarget> backupTargetsById = getBackupTargetsById(fileCopyPage);

        return fileCopyPage
                .map(fileCopy -> toFileCopyWithContext(fileCopy, gameFilesById, gamesById, backupTargetsById));
    }

    private Map<GameFileId, GameFile> getGameFilesById(Page<FileCopy> fileCopyPage) {
        Set<GameFileId> gameFileIds = fileCopyPage.content().stream()
                .map(fileCopy -> fileCopy.getNaturalId().gameFileId())
                .collect(toSet());

        return gameFileRepository.findAllByIdIn(gameFileIds).stream()
                .collect(toMap(GameFile::getId, identity()));
    }

    private Map<GameId, Game> getGamesById(Map<GameFileId, GameFile> gameFilesById) {
        Set<GameId> gameIds = gameFilesById.values().stream()
                .map(GameFile::getGameId)
                .collect(toSet());

        return gameRepository.findAllByIdIn(gameIds).stream()
                .collect(toMap(Game::getId, identity()));
    }

    private FileCopyWithContext toFileCopyWithContext(FileCopy fileCopy, Map<GameFileId, GameFile> gameFilesById,
                                                      Map<GameId, Game> gamesById,
                                                      Map<BackupTargetId, BackupTarget> backupTargetsById) {
        GameFile gameFile = gameFilesById.get(fileCopy.getNaturalId().gameFileId());
        Game game = gamesById.get(gameFile.getGameId());
        BackupTarget backupTarget = backupTargetsById.get(fileCopy.getNaturalId().backupTargetId());

        return new FileCopyWithContext(fileCopy, gameFile, game, backupTarget);
    }

    private Map<BackupTargetId, BackupTarget> getBackupTargetsById(Page<FileCopy> fileCopyPage) {
        Set<BackupTargetId> backupTargetIds = fileCopyPage.content().stream()
                .map(fileCopy -> fileCopy.getNaturalId().backupTargetId())
                .collect(toSet());

        return backupTargetRepository.findAllByIdIn(backupTargetIds).stream()
                .collect(toMap(BackupTarget::getId, identity()));
    }
}
