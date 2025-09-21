package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed;

import dev.codesoapbox.backity.core.backup.application.TrackableFileStream;
import dev.codesoapbox.backity.core.backup.application.writeprogress.OutputStreamProgressTracker;
import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogAuthService;
import dev.codesoapbox.backity.gameproviders.shared.infrastructure.adapters.driven.api.spring.DataBufferFluxTrackableFileStream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GogFileBackupServiceTest {

    @InjectMocks
    private GogFileBackupService gogFileBackupService;

    @Mock
    private GogEmbedWebClient gogFileProvider;

    @Mock
    private GogAuthService authService;

    @Nested
    class AcquireTrackableFileStream {

        @Test
        void acquireTrackableFileShouldReturnFileStream() {
            GameFile gameFile = TestGameFile.gog();
            OutputStreamProgressTracker outputStreamProgressTracker = mock(OutputStreamProgressTracker.class);
            TrackableFileStream trackableFileStream =
                    mockOutputStreamProgressTrackerAwareFileStreamCreation(gameFile, outputStreamProgressTracker);

            TrackableFileStream result =
                    gogFileBackupService.acquireTrackableFileStream(gameFile, outputStreamProgressTracker);

            assertThat(result).isEqualTo(trackableFileStream);
        }

        private TrackableFileStream mockOutputStreamProgressTrackerAwareFileStreamCreation(
                GameFile gameFile, OutputStreamProgressTracker outputStreamProgressTracker) {
            DataBufferFluxTrackableFileStream trackableFileStream = mock(DataBufferFluxTrackableFileStream.class);
            when(gogFileProvider.initializeProgressAndStreamFile(gameFile.getFileSource(), outputStreamProgressTracker))
                    .thenReturn(trackableFileStream);
            return trackableFileStream;
        }
    }

    @Nested
    class IsConnected {

        @Test
        void isConnectedShouldReturnTrueIfConnected() {
            when(authService.isAuthenticated())
                    .thenReturn(false)
                    .thenReturn(true);

            assertThat(gogFileBackupService.isConnected()).isFalse();
            assertThat(gogFileBackupService.isConnected()).isTrue();
        }
    }

    @Nested
    class GetGameProviderId {

        @Test
        void shouldGetGameProviderId() {
            assertThat(gogFileBackupService.getGameProviderId()).isEqualTo(new GameProviderId("GOG"));
        }
    }
}