package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.webclient;

import dev.codesoapbox.backity.core.backup.application.writeprogress.OutputStreamProgressTracker;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogGameWithFiles;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.webclient.operations.GetGameDetailsGogEmbedOperation;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.webclient.operations.GetLibraryGameIdsGogEmbedOperation;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.webclient.operations.GetLibrarySizeGogEmbedOperation;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.webclient.operations.InitializeProgressAndStreamFileGogEmbedOperation;
import dev.codesoapbox.backity.gameproviders.shared.infrastructure.adapters.driven.api.spring.DataBufferFluxTrackableFileStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GogEmbedWebClientIT {

    private GogEmbedWebClient gogEmbedClient;

    @Mock
    private GetLibrarySizeGogEmbedOperation getLibrarySizeOperation;

    @Mock
    private GetGameDetailsGogEmbedOperation getGameDetailsOperation;

    @Mock
    private GetLibraryGameIdsGogEmbedOperation getLibraryGameIdsOperation;

    @Mock
    private InitializeProgressAndStreamFileGogEmbedOperation initializeProgressAndStreamFileOperation;

    @BeforeEach
    void setUp() {
        gogEmbedClient = new GogEmbedWebClient(
                getLibrarySizeOperation,
                getGameDetailsOperation,
                getLibraryGameIdsOperation,
                initializeProgressAndStreamFileOperation
        );
    }

    @Nested
    class GetGameDetails {

        @Test
        void shouldDelegateToOperation() {
            String gameId = "someGameId";
            GogGameWithFiles expectedResult = mock(GogGameWithFiles.class);
            getGameDetailsOperationExecutes(gameId, expectedResult);

            Optional<GogGameWithFiles> result = gogEmbedClient.getGameDetails(gameId);

            assertThat(result).get().isEqualTo(expectedResult);
        }

        private void getGameDetailsOperationExecutes(String gameId, GogGameWithFiles expectedResult) {
            when(getGameDetailsOperation.execute(gameId))
                    .thenReturn(Optional.of(expectedResult));
        }
    }

    @Nested
    class GetLibrarySize {

        @Test
        void shouldDelegateToOperation() {
            String expectedResult = "10 GB";
            getLibrarySizeOperationExecutes(expectedResult);

            String result = gogEmbedClient.getLibrarySize();

            assertThat(result).isEqualTo(expectedResult);
        }

        private void getLibrarySizeOperationExecutes(String expectedResult) {
            when(getLibrarySizeOperation.execute())
                    .thenReturn(expectedResult);
        }
    }

    @Nested
    class GetLibraryGameIds {

        @Test
        void shouldDelegateToOperation() {
            List<String> expectedResult = List.of("1", "2", "3");
            getLibraryGameIdsOperationExecutes(expectedResult);

            List<String> result = gogEmbedClient.getLibraryGameIds();

            assertThat(result).isEqualTo(expectedResult);
        }

        private void getLibraryGameIdsOperationExecutes(List<String> expectedResult) {
            when(getLibraryGameIdsOperation.execute())
                    .thenReturn(expectedResult);
        }
    }

    @Nested
    class InitializeProgressAndStreamFile {

        @Test
        void shouldDelegateToOperation() {
            SourceFile sourceFile = mock(SourceFile.class);
            OutputStreamProgressTracker progress = mock(OutputStreamProgressTracker.class);
            DataBufferFluxTrackableFileStream expectedResult = mock(DataBufferFluxTrackableFileStream.class);
            initializeProgressAndStreamFileOperationExecutes(sourceFile, progress, expectedResult);

            DataBufferFluxTrackableFileStream result = gogEmbedClient
                    .initializeProgressAndStreamFile(sourceFile, progress);

            assertThat(result).isEqualTo(expectedResult);
        }

        private void initializeProgressAndStreamFileOperationExecutes(
                SourceFile sourceFile, OutputStreamProgressTracker progress,
                DataBufferFluxTrackableFileStream expectedResult) {
            when(initializeProgressAndStreamFileOperation.execute(sourceFile, progress))
                    .thenReturn(expectedResult);
        }
    }
}