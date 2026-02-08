package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.writeprogress.OutputStreamProgressTracker;
import dev.codesoapbox.backity.core.backup.application.writeprogress.OutputStreamProgressTrackerFactory;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;
import dev.codesoapbox.backity.core.sourcefile.domain.TestSourceFile;
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

    private void gameProviderExistsFor(SourceFile sourceFile) {
        when(gameProviderFileBackupService.getGameProviderId())
                .thenReturn(sourceFile.getGameProviderId());
    }

    @Nested
    class ReplicateFile {

        @Test
        void shouldReplicateFile() {
            StorageSolution storageSolution = mock(StorageSolution.class);
            SourceFile sourceFile = TestSourceFile.gog();
            FileCopy fileCopy = TestFileCopy.inProgress();
            gameProviderExistsFor(sourceFile);
            OutputStreamProgressTracker outputStreamProgressTracker = outputStreamProgressIsProvidedFor(fileCopy);
            TrackableFileStream fileStream = gameProviderProvidesFileStream(sourceFile, outputStreamProgressTracker);
            var fileCopyReplicator = new FileCopyReplicator(
                    List.of(gameProviderFileBackupService), outputStreamProgressTrackerFactory, storageSolutionWriteService);

            fileCopyReplicator.replicate(storageSolution, sourceFile, fileCopy);

            verify(storageSolutionWriteService).writeFileToStorage(fileStream, storageSolution, fileCopy.getFilePath());
        }

        private TrackableFileStream gameProviderProvidesFileStream(
                SourceFile sourceFile, OutputStreamProgressTracker outputStreamProgressTracker) {
            TrackableFileStream fileStream = mock(TrackableFileStream.class);
            when(gameProviderFileBackupService.acquireTrackableFileStream(
                    sourceFile, outputStreamProgressTracker))
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
            SourceFile sourceFile = TestSourceFile.gog();
            FileCopy fileCopy = TestFileCopy.enqueued();
            var fileCopyReplicator = new FileCopyReplicator(
                    List.of(gameProviderFileBackupService), outputStreamProgressTrackerFactory, storageSolutionWriteService);

            assertThatThrownBy(() -> fileCopyReplicator.replicate(storageSolution, sourceFile, fileCopy))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Cannot replicate file copy that is not in progress (id=" + fileCopy.getId() + ")");
        }

        @Test
        void shouldThrowGivenGameProviderDoesNotExist() {
            StorageSolution storageSolution = mock(StorageSolution.class);
            SourceFile sourceFile = TestSourceFile.gog();
            FileCopy fileCopy = TestFileCopy.inProgress();
            var fileCopyReplicator = new FileCopyReplicator(
                    List.of(gameProviderFileBackupService), outputStreamProgressTrackerFactory, storageSolutionWriteService);

            assertThatThrownBy(() -> fileCopyReplicator.replicate(storageSolution, sourceFile, fileCopy))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("File backup service for gameProviderId not found: "
                            + sourceFile.getGameProviderId());
        }
    }

    @Nested
    class GameProviderIsConnected {

        @Test
        void shouldReturnTrueGivenConnected() {
            SourceFile sourceFile = TestSourceFile.gog();
            gameProviderExistsFor(sourceFile);
            mockGameProviderIsConnected();
            var fileCopyReplicator = new FileCopyReplicator(
                    List.of(gameProviderFileBackupService), outputStreamProgressTrackerFactory, storageSolutionWriteService);

            boolean result = fileCopyReplicator.gameProviderIsConnected(sourceFile);

            assertThat(result).isTrue();
        }

        private void mockGameProviderIsConnected() {
            when(gameProviderFileBackupService.isConnected())
                    .thenReturn(true);
        }

        @Test
        void shouldReturnFalseGivenNotConnected() {
            SourceFile sourceFile = TestSourceFile.gog();
            gameProviderExistsFor(sourceFile);
            gameProviderIsNotConnected();
            var fileCopyReplicator = new FileCopyReplicator(
                    List.of(gameProviderFileBackupService), outputStreamProgressTrackerFactory, storageSolutionWriteService);

            boolean result = fileCopyReplicator.gameProviderIsConnected(sourceFile);

            assertThat(result).isFalse();
        }

        private void gameProviderIsNotConnected() {
            when(gameProviderFileBackupService.isConnected())
                    .thenReturn(false);
        }
    }
}