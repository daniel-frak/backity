package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.exceptions.FileWriteWasCanceledException;
import dev.codesoapbox.backity.core.backup.domain.exceptions.FileBackupFailedException;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyFailureReason;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;
import dev.codesoapbox.backity.core.storagesolution.domain.FakeUnixStorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.FilePath;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.UniqueFilePathResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileBackupServiceTest {

    private FileBackupService fileBackupService;

    @Mock
    private UniqueFilePathResolver uniqueFilePathResolver;

    @Mock
    private FileCopyRepository fileCopyRepository;

    @Mock
    private FileCopyReplicator fileCopyReplicator;

    @BeforeEach
    void setUp() {
        fileBackupService = new FileBackupService(uniqueFilePathResolver, fileCopyRepository, fileCopyReplicator);
    }

    @Nested
    class BackUpFile {

        private void gameProviderIsConnectedFor(SourceFile sourceFile) {
            when(fileCopyReplicator.gameProviderIsConnected(sourceFile))
                    .thenReturn(true);
        }

        private FilePath aUniqueFilePathIsResolvedFor(FileBackupContext fileBackupContext) {
            var filePath = new FilePath("someFileDir/someFile");
            when(uniqueFilePathResolver.resolve(
                    fileBackupContext.backupTarget().getPathTemplate(),
                    fileBackupContext.sourceFile(),
                    fileBackupContext.storageSolution()))
                    .thenReturn(filePath);

            return filePath;
        }

        private void assertOnlyPersistedStatusChangesWere(PersistedChangesToFileCopy persistedChangesToFileCopy,
                                                          List<FileCopyStatus> statusChanges) {
            assertThat(persistedChangesToFileCopy.savedFileCopyStatuses())
                    .isEqualTo(statusChanges);
        }

        @SuppressWarnings("SameParameterValue")
        private void assertLastPersistedChangeWas(PersistedChangesToFileCopy persistedChangesToFileCopy,
                                                  FileCopyStatus expectedStatus) {
            assertThat(persistedChangesToFileCopy.savedFileCopyStatuses().getLast())
                    .isEqualTo(expectedStatus);
        }

        private PersistedChangesToFileCopy trackPersistedChangesToFileCopy() {
            var persistedChanges = new PersistedChangesToFileCopy();
            doAnswer(a -> {
                FileCopy fileCopy = a.getArgument(0, FileCopy.class);
                persistedChanges.addFor(fileCopy);

                return fileCopy;
            }).when(fileCopyRepository).save(any());

            return persistedChanges;
        }

        private record PersistedChangesToFileCopy(
                List<FileCopyStatus> savedFileCopyStatuses,
                List<FilePath> savedFilePaths
        ) {

            private PersistedChangesToFileCopy() {
                this(new ArrayList<>(), new ArrayList<>());
            }

            public void addFor(FileCopy fileCopy) {
                savedFileCopyStatuses.add(fileCopy.getStatus());
                savedFilePaths.add(fileCopy.getFilePath());
            }
        }

        @Nested
        class DoNothing {

            @Test
            void shouldDoNothingGivenGameProviderNotConnected() {
                FileBackupContext fileBackupContext = TestFileBackupContext.trackedLocalGog();
                gameProviderIsNotConnected(fileBackupContext.sourceFile());

                fileBackupService.backUpFile(fileBackupContext);

                verifyNoMoreInteractions(uniqueFilePathResolver, fileCopyRepository, fileCopyReplicator);
            }

            private void gameProviderIsNotConnected(SourceFile sourceFile) {
                when(fileCopyReplicator.gameProviderIsConnected(sourceFile))
                        .thenReturn(false);
            }
        }

        @Nested
        class Successful {

            @Test
            void shouldBackupFile() {
                FileBackupContext fileBackupContext = TestFileBackupContext.enqueuedLocalGog();
                gameProviderIsConnectedFor(fileBackupContext.sourceFile());
                PersistedChangesToFileCopy persistedChangesToFileCopy = trackPersistedChangesToFileCopy();
                FilePath expectedFilePath = aUniqueFilePathIsResolvedFor(fileBackupContext);

                fileBackupService.backUpFile(fileBackupContext);

                assertOnlyPersistedStatusChangesWere(persistedChangesToFileCopy,
                        List.of(FileCopyStatus.IN_PROGRESS, FileCopyStatus.STORED_INTEGRITY_UNKNOWN));
                assertFilePathWasPersisted(persistedChangesToFileCopy, expectedFilePath);
                verify(fileCopyReplicator).replicate(
                        fileBackupContext.storageSolution(),
                        fileBackupContext.sourceFile(),
                        fileBackupContext.fileCopy());
            }

            private void assertFilePathWasPersisted(
                    PersistedChangesToFileCopy persistedChangesToFileCopy, FilePath expectedFilePath) {
                assertThat(persistedChangesToFileCopy.savedFilePaths())
                        .isEqualTo(Arrays.asList(
                                expectedFilePath, // Mark 'in progress'
                                expectedFilePath // Mark 'stored'
                        ));
            }
        }

        @Nested
        class Canceling {

            @Test
            void shouldMarkFileCopyAsTracked() {
                var storageSolution = new FakeUnixStorageSolution();
                var aFilePath = new FilePath("someFilePath");
                FileBackupContext fileBackupContext = TestFileBackupContext.enqueuedLocalGogBuilder()
                        .storageSolution(storageSolution)
                        .build();
                gameProviderIsConnectedFor(fileBackupContext.sourceFile());
                PersistedChangesToFileCopy persistedChangesToFileCopy = trackPersistedChangesToFileCopy();
                aUniqueFilePathIsResolvedFor(fileBackupContext);
                doThrow(new FileWriteWasCanceledException(aFilePath, storageSolution))
                        .when(fileCopyReplicator).replicate(storageSolution, fileBackupContext.sourceFile(),
                                fileBackupContext.fileCopy());

                fileBackupService.backUpFile(fileBackupContext);

                assertLastPersistedChangeWas(persistedChangesToFileCopy, FileCopyStatus.TRACKED);
            }

            @Test
            void shouldDeleteFile() {
                var storageSolution = new FakeUnixStorageSolution();
                FileBackupContext fileBackupContext = TestFileBackupContext.enqueuedLocalGogBuilder()
                        .storageSolution(storageSolution)
                        .build();
                FilePath filePath = aUniqueFilePathIsResolvedFor(fileBackupContext);
                storageSolution.createFile(filePath);
                gameProviderIsConnectedFor(fileBackupContext.sourceFile());
                doThrow(new FileWriteWasCanceledException(filePath, storageSolution))
                        .when(fileCopyReplicator).replicate(storageSolution, fileBackupContext.sourceFile(),
                                fileBackupContext.fileCopy());

                fileBackupService.backUpFile(fileBackupContext);

                assertThat(storageSolution.fileExists(filePath)).isFalse();
            }

            @Test
            void shouldUnsetFileCopyFilePath() {
                var storageSolution = new FakeUnixStorageSolution();
                FileBackupContext fileBackupContext = TestFileBackupContext.enqueuedLocalGogBuilder()
                        .storageSolution(storageSolution)
                        .build();
                FilePath filePath = aUniqueFilePathIsResolvedFor(fileBackupContext);
                gameProviderIsConnectedFor(fileBackupContext.sourceFile());
                doThrow(new FileWriteWasCanceledException(filePath, storageSolution))
                        .when(fileCopyReplicator).replicate(storageSolution, fileBackupContext.sourceFile(),
                                fileBackupContext.fileCopy());

                fileBackupService.backUpFile(fileBackupContext);

                assertThat(fileBackupContext.fileCopy().getFilePath()).isNull();
            }
        }

        @Nested
        class Throwing {

            private void assertIsFailedWithReason(FileCopy fileCopy, Throwable coreException) {
                assertThat(fileCopy)
                        .satisfies(it -> assertSoftly(softly -> {
                            softly.assertThat(it.getStatus()).isEqualTo(FileCopyStatus.FAILED);
                            softly.assertThat(it.getFailedReason().value()).isEqualTo(coreException.getMessage());
                        }));
            }

            private RuntimeException fileCopyReplicatorThrowsAfterCreatingFile(
                    FakeUnixStorageSolution storageSolution, FilePath filePath) {
                var coreException = new RuntimeException("someMessage");
                doAnswer(_ -> {
                    storageSolution.createFile(filePath);
                    throw coreException;
                }).when(fileCopyReplicator).replicate(any(), any(), any());

                return coreException;
            }

            @Nested
            class WhenFileCopyReplicatorThrows {

                @Test
                void shouldRethrowWrapped() {
                    var storageSolution = new FakeUnixStorageSolution();
                    FileBackupContext fileBackupContext = TestFileBackupContext.enqueuedLocalGogBuilder()
                            .storageSolution(storageSolution)
                            .build();
                    gameProviderIsConnectedFor(fileBackupContext.sourceFile());
                    FilePath filePath = aUniqueFilePathIsResolvedFor(fileBackupContext);
                    RuntimeException coreException = fileCopyReplicatorThrowsAfterCreatingFile(
                            storageSolution, filePath);

                    assertThatThrownBy(() ->
                            fileBackupService.backUpFile(fileBackupContext))
                            .isInstanceOf(FileBackupFailedException.class)
                            .hasCause(coreException);
                }

                @Test
                void shouldMarkFileCopyAsFailedWithReason() {
                    var storageSolution = new FakeUnixStorageSolution();
                    FileBackupContext fileBackupContext = TestFileBackupContext.enqueuedLocalGogBuilder()
                            .storageSolution(storageSolution)
                            .build();
                    gameProviderIsConnectedFor(fileBackupContext.sourceFile());
                    FilePath filePath = aUniqueFilePathIsResolvedFor(fileBackupContext);
                    RuntimeException coreException = fileCopyReplicatorThrowsAfterCreatingFile(
                            storageSolution, filePath);

                    assertThatThrownBy(() -> fileBackupService.backUpFile(fileBackupContext))
                            .isInstanceOf(Throwable.class);

                    assertIsFailedWithReason(fileBackupContext.fileCopy(), coreException);
                    verify(fileCopyRepository, times(2)).save(fileBackupContext.fileCopy());
                }

                @SuppressWarnings("ThrowableNotThrown")
                @Test
                void shouldRemoveFile() {
                    var storageSolution = new FakeUnixStorageSolution();
                    FileBackupContext fileBackupContext = TestFileBackupContext.enqueuedLocalGogBuilder()
                            .storageSolution(storageSolution)
                            .build();
                    gameProviderIsConnectedFor(fileBackupContext.sourceFile());
                    FilePath filePath = aUniqueFilePathIsResolvedFor(fileBackupContext);
                    fileCopyReplicatorThrowsAfterCreatingFile(storageSolution, filePath);

                    assertThatThrownBy(() -> fileBackupService.backUpFile(fileBackupContext))
                            .isInstanceOf(Throwable.class);
                    assertThat(storageSolution.fileExists(filePath)).isFalse();
                }
            }

            @Nested
            class WhenPathResolverThrows {

                @Test
                void shouldWrapException() {
                    var storageSolution = new FakeUnixStorageSolution();
                    FileBackupContext fileBackupContext = TestFileBackupContext.trackedLocalGogBuilder()
                            .storageSolution(storageSolution)
                            .build();
                    gameProviderIsConnectedFor(fileBackupContext.sourceFile());
                    RuntimeException coreException = pathResolverAlwaysThrows();

                    assertThatThrownBy(() ->
                            fileBackupService.backUpFile(fileBackupContext))
                            .isInstanceOf(FileBackupFailedException.class)
                            .hasCause(coreException);
                }

                private RuntimeException pathResolverAlwaysThrows() {
                    var coreException = new RuntimeException("Test exception");
                    when(uniqueFilePathResolver.resolve(any(), any(), any()))
                            .thenThrow(coreException);
                    return coreException;
                }

                @Test
                void shouldMarkAsFailedWithReason() {
                    var storageSolution = new FakeUnixStorageSolution();
                    FileBackupContext fileBackupContext = TestFileBackupContext.trackedLocalGogBuilder()
                            .storageSolution(storageSolution)
                            .build();
                    gameProviderIsConnectedFor(fileBackupContext.sourceFile());
                    RuntimeException coreException = pathResolverAlwaysThrows();

                    assertThatThrownBy(() -> fileBackupService.backUpFile(fileBackupContext))
                            .isInstanceOf(Throwable.class);

                    assertIsFailedWithReason(fileBackupContext.fileCopy(), coreException);
                    verify(fileCopyRepository, times(1)).save(fileBackupContext.fileCopy());
                    assertThat(fileBackupContext.fileCopy().getFilePath()).isNull();
                }

                @Test
                void shouldMarkFileCopyAsFailedWithUnknownErrorMessageGivenExceptionMessageIsNull() {
                    FileBackupContext fileBackupContext = TestFileBackupContext.trackedLocalGog();
                    gameProviderIsConnectedFor(fileBackupContext.sourceFile());
                    RuntimeException coreException = pathResolverThrowsWithNullMessage();

                    assertThatThrownBy(() ->
                            fileBackupService.backUpFile(fileBackupContext))
                            .isInstanceOf(FileBackupFailedException.class)
                            .hasCause(coreException);
                    assertThat(fileBackupContext.fileCopy().getFailedReason())
                            .isEqualTo(new FileCopyFailureReason("Unknown error"));
                }

                private RuntimeException pathResolverThrowsWithNullMessage() {
                    var coreException = mock(RuntimeException.class); // getMessage() will return null
                    when(uniqueFilePathResolver.resolve(any(), any(), any()))
                            .thenThrow(coreException);
                    return coreException;
                }

                @SuppressWarnings("ThrowableNotThrown")
                @Test
                void shouldNotTryToDeleteFileGivenFileCopyDoesNotHaveFilePath() {
                    StorageSolution storageSolution = mock();
                    FileBackupContext fileBackupContext = TestFileBackupContext.trackedLocalGogBuilder()
                            .storageSolution(storageSolution)
                            .build();
                    gameProviderIsConnectedFor(fileBackupContext.sourceFile());
                    pathResolverAlwaysThrows();

                    assertThatThrownBy(() -> fileBackupService.backUpFile(fileBackupContext))
                            .isInstanceOf(Throwable.class);

                    verify(storageSolution, never()).deleteIfExists(any());
                }
            }

            @Nested
            class WhenFileDeleteThrows {

                @Test
                void shouldWrapException() {
                    FakeUnixStorageSolution storageSolution = storageSolutionFailingOnFileDeletion();
                    var aFilePath = new FilePath("filePath");
                    FileBackupContext fileBackupContext = TestFileBackupContext.enqueuedLocalGogBuilder()
                            .storageSolution(storageSolution)
                            .build();
                    gameProviderIsConnectedFor(fileBackupContext.sourceFile());
                    aUniqueFilePathIsResolvedFor(fileBackupContext);
                    RuntimeException coreException =
                            fileCopyReplicatorThrowsAfterCreatingFile(storageSolution, aFilePath);

                    assertThatThrownBy(() ->
                            fileBackupService.backUpFile(fileBackupContext))
                            .isInstanceOf(FileBackupFailedException.class)
                            .hasCause(coreException);
                }

                private FakeUnixStorageSolution storageSolutionFailingOnFileDeletion() {
                    var storageSolution = new FakeUnixStorageSolution();
                    var fileDeletionException = new RuntimeException("File deletion exception");
                    storageSolution.setShouldThrowOnFileDeletion(fileDeletionException);
                    return storageSolution;
                }

                @Test
                void shouldMarkFileCopyAsFailedWithReason() {
                    FakeUnixStorageSolution storageSolution = storageSolutionFailingOnFileDeletion();
                    var aFilePath = new FilePath("filePath");
                    FileBackupContext fileBackupContext = TestFileBackupContext.enqueuedLocalGogBuilder()
                            .storageSolution(storageSolution)
                            .build();
                    gameProviderIsConnectedFor(fileBackupContext.sourceFile());
                    aUniqueFilePathIsResolvedFor(fileBackupContext);
                    RuntimeException coreException =
                            fileCopyReplicatorThrowsAfterCreatingFile(storageSolution, aFilePath);

                    assertThatThrownBy(() -> fileBackupService.backUpFile(fileBackupContext))
                            .isInstanceOf(Throwable.class);

                    assertIsFailedWithReason(fileBackupContext.fileCopy(), coreException);
                    verify(fileCopyRepository, times(2)).save(fileBackupContext.fileCopy());
                }

                @SuppressWarnings("ThrowableNotThrown")
                @Test
                void shouldNotUnsetFileCopyFilePath() {
                    FakeUnixStorageSolution storageSolution = storageSolutionFailingOnFileDeletion();
                    var aFilePath = new FilePath("filePath");
                    FileBackupContext fileBackupContext = TestFileBackupContext.enqueuedLocalGogBuilder()
                            .storageSolution(storageSolution)
                            .build();
                    gameProviderIsConnectedFor(fileBackupContext.sourceFile());
                    aUniqueFilePathIsResolvedFor(fileBackupContext);
                    fileCopyReplicatorThrowsAfterCreatingFile(storageSolution, aFilePath);

                    assertThatThrownBy(() -> fileBackupService.backUpFile(fileBackupContext))
                            .isInstanceOf(Throwable.class);

                    assertThat(fileBackupContext.fileCopy().getFilePath()).isNotNull();
                    verify(fileCopyRepository, times(2)).save(fileBackupContext.fileCopy());
                }
            }
        }
    }
}