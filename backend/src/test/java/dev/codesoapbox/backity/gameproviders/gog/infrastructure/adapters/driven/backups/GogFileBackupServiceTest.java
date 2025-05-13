package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.backups;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.BackupProgress;
import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.gameproviders.gog.application.TrackableFileStream;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.library.GogEmbedWebClient;
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
        BackupProgress backupProgress = mock(BackupProgress.class);
        TrackableFileStream trackableFileStream =
                mockProgressAwareFileStreamCreation(gameFile, backupProgress);

        gogFileBackupService.backUpFile(gameFile, backupProgress);

        String filePath = gameFile.getFileBackup().getFilePath();
        verify(urlFileDownloader).downloadFile(trackableFileStream, gameFile, filePath);
    }

    private TrackableFileStream mockProgressAwareFileStreamCreation(GameFile gameFile, BackupProgress backupProgress) {
        TrackableFileStream trackableFileStream = mock(TrackableFileStream.class);
        when(gogFileProvider.initializeProgressAndStreamFile(gameFile.getGameProviderFile(), backupProgress))
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