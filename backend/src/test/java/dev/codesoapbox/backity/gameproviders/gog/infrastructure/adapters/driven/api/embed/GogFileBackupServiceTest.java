package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.embed;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgress;
import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
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
    private UrlFileDownloader urlFileDownloader;

    @Test
    void backUpFileShouldDownloadFile() throws IOException {
        GameFile gameFile = TestGameFile.discovered();
        DownloadProgress downloadProgress = mock(DownloadProgress.class);
        TrackableFileStream trackableFileStream =
                mockProgressAwareFileStreamCreation(gameFile, downloadProgress);

        gogFileBackupService.backUpFile(gameFile, downloadProgress);

        String filePath = gameFile.getFileBackup().filePath();
        verify(urlFileDownloader).downloadFile(trackableFileStream, gameFile, filePath);
    }

    private TrackableFileStream mockProgressAwareFileStreamCreation(GameFile gameFile, DownloadProgress downloadProgress) {
        TrackableFileStream trackableFileStream = mock(TrackableFileStream.class);
        when(gogFileProvider.initializeProgressAndStreamFile(gameFile.getFileSource(), downloadProgress))
                .thenReturn(trackableFileStream);
        return trackableFileStream;
    }

    @Test
    void isReadyShouldReturnTrueIfReady() {
        when(authService.isAuthenticated())
                .thenReturn(false)
                .thenReturn(true);

        assertThat(gogFileBackupService.isReady()).isFalse();
        assertThat(gogFileBackupService.isReady()).isTrue();
    }

    @Test
    void shouldGetGameProviderId() {
        assertThat(gogFileBackupService.getGameProviderId()).isEqualTo(new GameProviderId("GOG"));
    }
}