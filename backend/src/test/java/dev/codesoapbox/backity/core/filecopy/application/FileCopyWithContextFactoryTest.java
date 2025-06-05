package dev.codesoapbox.backity.core.filecopy.application;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgress;
import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.core.backup.domain.TestFileCopyReplicationProgress;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.backuptarget.domain.TestBackupTarget;
import dev.codesoapbox.backity.core.filecopy.application.usecases.FileCopyWithContext;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.game.domain.TestGame;
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
class FileCopyWithContextFactoryTest {

    private FileCopyWithContextFactory fileCopyWithContextFactory;

    @Mock
    private GameFileRepository gameFileRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private BackupTargetRepository backupTargetRepository;

    @Mock
    private FileCopyReplicationProgressRepository replicationProgressRepository;

    @BeforeEach
    void setUp() {
        fileCopyWithContextFactory = new FileCopyWithContextFactory(
                gameFileRepository, gameRepository, backupTargetRepository, replicationProgressRepository);
    }

    @Test
    void shouldCreateFromPage() {
        var pagination = new Pagination(0, 1);
        FileCopy fileCopy = TestFileCopy.enqueued();
        Page<FileCopy> fileCopyPage = TestPage.of(List.of(fileCopy), pagination);
        GameFile gameFile = mockGameFileExists(fileCopy);
        Game game = mockGameExists(gameFile);
        BackupTarget backupTarget = mockBackupTargetExists(fileCopy);
        FileCopyReplicationProgress replicationProgress = mockReplicationProgressExists(fileCopy);

        Page<FileCopyWithContext> result = fileCopyWithContextFactory.createPageFrom(fileCopyPage);

        Page<FileCopyWithContext> expectedResult = TestPage.of(
                List.of(new FileCopyWithContext(fileCopy, gameFile, game, backupTarget, replicationProgress)),
                pagination);
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    private GameFile mockGameFileExists(FileCopy fileCopy) {
        GameFile gameFile = TestGameFile.gogBuilder()
                .id(fileCopy.getNaturalId().gameFileId())
                .build();
        when(gameFileRepository.findAllByIdIn(Set.of(fileCopy.getNaturalId().gameFileId())))
                .thenReturn(List.of(gameFile));

        return gameFile;
    }

    private Game mockGameExists(GameFile gameFile) {
        Game game = TestGame.anyBuilder()
                .withId(gameFile.getGameId())
                .build();
        when(gameRepository.findAllByIdIn(Set.of(gameFile.getGameId())))
                .thenReturn(List.of(game));

        return game;
    }

    private BackupTarget mockBackupTargetExists(FileCopy fileCopy) {
        BackupTarget backupTarget = TestBackupTarget.localFolderBuilder()
                .withId(fileCopy.getNaturalId().backupTargetId())
                .build();
        when(backupTargetRepository.findAllByIdIn(Set.of(fileCopy.getNaturalId().backupTargetId())))
                .thenReturn(List.of(backupTarget));

        return backupTarget;
    }

    private FileCopyReplicationProgress mockReplicationProgressExists(FileCopy fileCopy) {
        FileCopyReplicationProgress replicationProgress = TestFileCopyReplicationProgress.twentyFivePercent();
        when(replicationProgressRepository.findAllByFileCopyIdIn(Set.of(fileCopy.getId())))
                .thenReturn(List.of(replicationProgress));

        return replicationProgress;
    }
}