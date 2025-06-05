package dev.codesoapbox.backity.core.filecopy.application;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgress;
import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.filecopy.application.usecases.FileCopyWithContext;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
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

@RequiredArgsConstructor
public class FileCopyWithContextFactory {

    private final GameFileRepository gameFileRepository;
    private final GameRepository gameRepository;
    private final BackupTargetRepository backupTargetRepository;
    private final FileCopyReplicationProgressRepository fileCopyReplicationProgressRepository;

    public Page<FileCopyWithContext> createPageFrom(Page<FileCopy> fileCopyPage) {
        Map<GameFileId, GameFile> gameFilesById = getGameFilesById(fileCopyPage);
        Map<GameId, Game> gamesById = getGamesById(gameFilesById);
        Map<BackupTargetId, BackupTarget> backupTargetsById = getBackupTargetsById(fileCopyPage);
        Map<FileCopyId, FileCopyReplicationProgress> replicationProgressesByFileCopyId =
                getReplicationProgressesByFileCopyId(fileCopyPage);

        return fileCopyPage.map(fileCopy -> toFileCopyWithContext(
                fileCopy, gameFilesById, gamesById, backupTargetsById, replicationProgressesByFileCopyId));
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

    private Map<FileCopyId, FileCopyReplicationProgress> getReplicationProgressesByFileCopyId(
            Page<FileCopy> fileCopyPage) {
        Set<FileCopyId> fileCopyIds = fileCopyPage.content().stream()
                .map(FileCopy::getId)
                .collect(toSet());

        return fileCopyReplicationProgressRepository.findAllByFileCopyIdIn(fileCopyIds).stream()
                .collect(toMap(FileCopyReplicationProgress::fileCopyId, identity()));
    }

    private FileCopyWithContext toFileCopyWithContext(
            FileCopy fileCopy, Map<GameFileId, GameFile> gameFilesById,
            Map<GameId, Game> gamesById,
            Map<BackupTargetId, BackupTarget> backupTargetsById,
            Map<FileCopyId, FileCopyReplicationProgress> replicationProgressesByFileCopyId) {
        GameFile gameFile = gameFilesById.get(fileCopy.getNaturalId().gameFileId());
        Game game = gamesById.get(gameFile.getGameId());
        BackupTarget backupTarget = backupTargetsById.get(fileCopy.getNaturalId().backupTargetId());
        FileCopyReplicationProgress replicationProgress = replicationProgressesByFileCopyId.get(fileCopy.getId());

        return new FileCopyWithContext(fileCopy, gameFile, game, backupTarget, replicationProgress);
    }

    private Map<BackupTargetId, BackupTarget> getBackupTargetsById(Page<FileCopy> fileCopyPage) {
        Set<BackupTargetId> backupTargetIds = fileCopyPage.content().stream()
                .map(fileCopy -> fileCopy.getNaturalId().backupTargetId())
                .collect(toSet());

        return backupTargetRepository.findAllByIdIn(backupTargetIds).stream()
                .collect(toMap(BackupTarget::getId, identity()));
    }
}
