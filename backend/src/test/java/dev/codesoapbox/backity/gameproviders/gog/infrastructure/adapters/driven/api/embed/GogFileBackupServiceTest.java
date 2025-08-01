package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.embed;

import dev.codesoapbox.backity.core.backup.application.TrackableFileStream;
import dev.codesoapbox.backity.core.backup.application.DownloadService;
import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgress;
import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogAuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GogFileBackupServiceTest {

    @InjectMocks
    private GogFileBackupService gogFileBackupService;

    @Mock
    private GogEmbedWebClient gogFileProvider;

    @Mock
    private GogAuthService authService;

    @Mock
    private DownloadService downloadService;

    @Test
    void replicateFileShouldDownloadFile() throws IOException {
        GameFile gameFile = TestGameFile.gog();
        FileCopy fileCopy = TestFileCopy.tracked();
        DownloadProgress downloadProgress = mock(DownloadProgress.class);
        TrackableFileStream trackableFileStream =
                mockProgressAwareFileStreamCreation(gameFile, downloadProgress);
        StorageSolution storageSolution = mock(StorageSolution.class);

        gogFileBackupService.replicateFile(storageSolution, gameFile, fileCopy, downloadProgress);

        String filePath = fileCopy.getFilePath();
        verify(downloadService).downloadFile(storageSolution, trackableFileStream, gameFile, filePath);
    }

    private TrackableFileStream mockProgressAwareFileStreamCreation(
            GameFile gameFile, DownloadProgress downloadProgress) {
        TrackableFileStream trackableFileStream = mock(TrackableFileStream.class);
        when(gogFileProvider.initializeProgressAndStreamFile(gameFile.getFileSource(), downloadProgress))
                .thenReturn(trackableFileStream);
        return trackableFileStream;
    }

    @Test
    void isConnectedShouldReturnTrueIfConnected() {
        when(authService.isAuthenticated())
                .thenReturn(false)
                .thenReturn(true);

        assertThat(gogFileBackupService.isConnected()).isFalse();
        assertThat(gogFileBackupService.isConnected()).isTrue();
    }

    @Test
    void shouldGetGameProviderId() {
        assertThat(gogFileBackupService.getGameProviderId()).isEqualTo(new GameProviderId("GOG"));
    }
}