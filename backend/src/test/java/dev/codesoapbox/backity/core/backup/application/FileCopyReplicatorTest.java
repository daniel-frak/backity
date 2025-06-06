package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgress;
import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgressFactory;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileCopyReplicatorTest {

    @Mock
    private GameProviderFileBackupService gameProviderFileBackupService;

    @Mock
    private DownloadProgressFactory downloadProgressFactory;

    @Test
    void shouldReplicateFile() throws IOException {
        StorageSolution storageSolution = mock(StorageSolution.class);
        GameFile gameFile = TestGameFile.gog();
        FileCopy fileCopy = TestFileCopy.inProgress();
        mockGameProviderExistsFor(gameFile);
        DownloadProgress downloadProgress = mockDownloadProgressCreation(fileCopy);
        var fileCopyReplicator = new FileCopyReplicator(
                List.of(gameProviderFileBackupService), downloadProgressFactory);

        fileCopyReplicator.replicateFile(storageSolution, gameFile, fileCopy);

        verify(gameProviderFileBackupService).replicateFile(storageSolution, gameFile, fileCopy, downloadProgress);
    }

    private void mockGameProviderExistsFor(GameFile gameFile) {
        when(gameProviderFileBackupService.getGameProviderId())
                .thenReturn(gameFile.getFileSource().gameProviderId());
    }

    private DownloadProgress mockDownloadProgressCreation(FileCopy fileCopy) {
        DownloadProgress downloadProgress = mock(DownloadProgress.class);
        when(downloadProgressFactory.create(fileCopy))
                .thenReturn(downloadProgress);
        return downloadProgress;
    }

    @Test
    void replicateFileShouldThrowGivenFileCopyNotInProgress() {
        StorageSolution storageSolution = mock(StorageSolution.class);
        GameFile gameFile = TestGameFile.gog();
        FileCopy fileCopy = TestFileCopy.enqueued();
        var fileCopyReplicator = new FileCopyReplicator(
                List.of(gameProviderFileBackupService), downloadProgressFactory);

        assertThatThrownBy(() -> fileCopyReplicator.replicateFile(storageSolution, gameFile, fileCopy))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot replicate file copy that is not in progress (id=" + fileCopy.getId() + ")");
    }

    @Test
    void replicateFileShouldThrowGivenGameProviderDoesNotExist() {
        StorageSolution storageSolution = mock(StorageSolution.class);
        GameFile gameFile = TestGameFile.gog();
        FileCopy fileCopy = TestFileCopy.inProgress();
        var fileCopyReplicator = new FileCopyReplicator(
                List.of(gameProviderFileBackupService), downloadProgressFactory);

        assertThatThrownBy(() -> fileCopyReplicator.replicateFile(storageSolution, gameFile, fileCopy))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("File backup service for gameProviderId not found: "
                            + gameFile.getFileSource().gameProviderId());
    }

    @Test
    void gameProviderIsConnectedShouldReturnTrueGivenConnected() {
        GameFile gameFile = TestGameFile.gog();
        mockGameProviderExistsFor(gameFile);
        mockGameProviderIsConnected();
        var fileCopyReplicator = new FileCopyReplicator(
                List.of(gameProviderFileBackupService), downloadProgressFactory);

        boolean result = fileCopyReplicator.gameProviderIsConnected(gameFile);

        assertThat(result).isTrue();
    }

    private void mockGameProviderIsConnected() {
        when(gameProviderFileBackupService.isConnected())
                .thenReturn(true);
    }

    @Test
    void gameProviderIsConnectedShouldReturnFalseGivenNotConnected() {
        GameFile gameFile = TestGameFile.gog();
        mockGameProviderExistsFor(gameFile);
        mockGameProviderIsNotConnected();
        var fileCopyReplicator = new FileCopyReplicator(
                List.of(gameProviderFileBackupService), downloadProgressFactory);

        boolean result = fileCopyReplicator.gameProviderIsConnected(gameFile);

        assertThat(result).isFalse();
    }

    private void mockGameProviderIsNotConnected() {
        when(gameProviderFileBackupService.isConnected())
                .thenReturn(false);
    }
}