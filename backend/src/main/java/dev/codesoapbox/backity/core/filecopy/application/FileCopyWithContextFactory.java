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
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFileId;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFileRepository;
import dev.codesoapbox.backity.shared.domain.Page;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Set;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@RequiredArgsConstructor
public class FileCopyWithContextFactory {

    private final SourceFileRepository sourceFileRepository;
    private final GameRepository gameRepository;
    private final BackupTargetRepository backupTargetRepository;
    private final FileCopyReplicationProgressRepository fileCopyReplicationProgressRepository;

    public Page<FileCopyWithContext> createPageFrom(Page<FileCopy> fileCopyPage) {
        Map<SourceFileId, SourceFile> sourceFilesById = getSourceFilesById(fileCopyPage);
        Map<GameId, Game> gamesById = getGamesById(sourceFilesById);
        Map<BackupTargetId, BackupTarget> backupTargetsById = getBackupTargetsById(fileCopyPage);
        Map<FileCopyId, FileCopyReplicationProgress> replicationProgressesByFileCopyId =
                getReplicationProgressesByFileCopyId(fileCopyPage);

        return fileCopyPage.map(fileCopy -> toFileCopyWithContext(
                fileCopy, sourceFilesById, gamesById, backupTargetsById, replicationProgressesByFileCopyId));
    }

    private Map<SourceFileId, SourceFile> getSourceFilesById(Page<FileCopy> fileCopyPage) {
        Set<SourceFileId> sourceFileIds = fileCopyPage.content().stream()
                .map(fileCopy -> fileCopy.getNaturalId().sourceFileId())
                .collect(toSet());

        return sourceFileRepository.findAllByIdIn(sourceFileIds).stream()
                .collect(toMap(SourceFile::getId, identity()));
    }

    private Map<GameId, Game> getGamesById(Map<SourceFileId, SourceFile> sourceFilesById) {
        Set<GameId> gameIds = sourceFilesById.values().stream()
                .map(SourceFile::getGameId)
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
            FileCopy fileCopy, Map<SourceFileId, SourceFile> sourceFilesById,
            Map<GameId, Game> gamesById,
            Map<BackupTargetId, BackupTarget> backupTargetsById,
            Map<FileCopyId, FileCopyReplicationProgress> replicationProgressesByFileCopyId) {
        SourceFile sourceFile = sourceFilesById.get(fileCopy.getNaturalId().sourceFileId());
        Game game = gamesById.get(sourceFile.getGameId());
        BackupTarget backupTarget = backupTargetsById.get(fileCopy.getNaturalId().backupTargetId());
        FileCopyReplicationProgress replicationProgress = replicationProgressesByFileCopyId.get(fileCopy.getId());

        return new FileCopyWithContext(fileCopy, sourceFile, game, backupTarget, replicationProgress);
    }

    private Map<BackupTargetId, BackupTarget> getBackupTargetsById(Page<FileCopy> fileCopyPage) {
        Set<BackupTargetId> backupTargetIds = fileCopyPage.content().stream()
                .map(fileCopy -> fileCopy.getNaturalId().backupTargetId())
                .collect(toSet());

        return backupTargetRepository.findAllByIdIn(backupTargetIds).stream()
                .collect(toMap(BackupTarget::getId, identity()));
    }
}
