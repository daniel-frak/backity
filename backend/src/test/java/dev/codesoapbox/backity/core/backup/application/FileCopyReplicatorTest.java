package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.writeprogress.OutputStreamProgressTracker;
import dev.codesoapbox.backity.core.backup.application.writeprogress.OutputStreamProgressTrackerFactory;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileCopyReplicatorTest {

    @Mock
    private GameProviderFileBackupService gameProviderFileBackupService;

    @Mock
    private OutputStreamProgressTrackerFactory outputStreamProgressTrackerFactory;

    @Mock
    private StorageSolutionWriteService storageSolutionWriteService;

    private void gameProviderExistsFor(GameFile gameFile) {
        when(gameProviderFileBackupService.getGameProviderId())
                .thenReturn(gameFile.getFileSource().gameProviderId());
    }

    @Nested
    class ReplicateFile {

        @Test
        void shouldReplicateFile() {
            StorageSolution storageSolution = mock(StorageSolution.class);
            GameFile gameFile = TestGameFile.gog();
            FileCopy fileCopy = TestFileCopy.inProgress();
            gameProviderExistsFor(gameFile);
            OutputStreamProgressTracker outputStreamProgressTracker = outputStreamProgressIsProvidedFor(fileCopy);
            TrackableFileStream fileStream = gameProviderProvidesFileStream(gameFile, outputStreamProgressTracker);
            var fileCopyReplicator = new FileCopyReplicator(
                    List.of(gameProviderFileBackupService), outputStreamProgressTrackerFactory, storageSolutionWriteService);

            fileCopyReplicator.replicate(storageSolution, gameFile, fileCopy);

            verify(storageSolutionWriteService).writeFileToStorage(fileStream, storageSolution, fileCopy.getFilePath());
        }

        private TrackableFileStream gameProviderProvidesFileStream(
                GameFile gameFile, OutputStreamProgressTracker outputStreamProgressTracker) {
            TrackableFileStream fileStream = mock(TrackableFileStream.class);
            when(gameProviderFileBackupService.acquireTrackableFileStream(
                    gameFile, outputStreamProgressTracker))
                    .thenReturn(fileStream);
            return fileStream;
        }

        private OutputStreamProgressTracker outputStreamProgressIsProvidedFor(FileCopy fileCopy) {
            OutputStreamProgressTracker outputStreamProgressTracker = mock(OutputStreamProgressTracker.class);
            when(outputStreamProgressTrackerFactory.create(fileCopy))
                    .thenReturn(outputStreamProgressTracker);
            return outputStreamProgressTracker;
        }

        @Test
        void shouldThrowGivenFileCopyNotInProgress() {
            StorageSolution storageSolution = mock(StorageSolution.class);
            GameFile gameFile = TestGameFile.gog();
            FileCopy fileCopy = TestFileCopy.enqueued();
            var fileCopyReplicator = new FileCopyReplicator(
                    List.of(gameProviderFileBackupService), outputStreamProgressTrackerFactory, storageSolutionWriteService);

            assertThatThrownBy(() -> fileCopyReplicator.replicate(storageSolution, gameFile, fileCopy))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Cannot replicate file copy that is not in progress (id=" + fileCopy.getId() + ")");
        }

        @Test
        void shouldThrowGivenGameProviderDoesNotExist() {
            StorageSolution storageSolution = mock(StorageSolution.class);
            GameFile gameFile = TestGameFile.gog();
            FileCopy fileCopy = TestFileCopy.inProgress();
            var fileCopyReplicator = new FileCopyReplicator(
                    List.of(gameProviderFileBackupService), outputStreamProgressTrackerFactory, storageSolutionWriteService);

            assertThatThrownBy(() -> fileCopyReplicator.replicate(storageSolution, gameFile, fileCopy))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("File backup service for gameProviderId not found: "
                            + gameFile.getFileSource().gameProviderId());
        }
    }

    @Nested
    class GameProviderIsConnected {

        @Test
        void shouldReturnTrueGivenConnected() {
            GameFile gameFile = TestGameFile.gog();
            gameProviderExistsFor(gameFile);
            mockGameProviderIsConnected();
            var fileCopyReplicator = new FileCopyReplicator(
                    List.of(gameProviderFileBackupService), outputStreamProgressTrackerFactory, storageSolutionWriteService);

            boolean result = fileCopyReplicator.gameProviderIsConnected(gameFile);

            assertThat(result).isTrue();
        }

        private void mockGameProviderIsConnected() {
            when(gameProviderFileBackupService.isConnected())
                    .thenReturn(true);
        }

        @Test
        void shouldReturnFalseGivenNotConnected() {
            GameFile gameFile = TestGameFile.gog();
            gameProviderExistsFor(gameFile);
            gameProviderIsNotConnected();
            var fileCopyReplicator = new FileCopyReplicator(
                    List.of(gameProviderFileBackupService), outputStreamProgressTrackerFactory, storageSolutionWriteService);

            boolean result = fileCopyReplicator.gameProviderIsConnected(gameFile);

            assertThat(result).isFalse();
        }

        private void gameProviderIsNotConnected() {
            when(gameProviderFileBackupService.isConnected())
                    .thenReturn(false);
        }
    }
}