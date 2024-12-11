package dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services;

import dev.codesoapbox.backity.core.backup.domain.BackupProgress;
import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services.embed.GogEmbedWebClient;
import dev.codesoapbox.backity.integrations.gog.domain.services.GogAuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static dev.codesoapbox.backity.core.gamefile.domain.TestGameFile.discoveredGameFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GogFileBackupServiceTest {

    @InjectMocks
    private GogFileBackupService gogFileBackupService;

    @Mock
    private GogEmbedWebClient gogEmbedClient;

    @Mock
    private GogAuthService authService;

    @Mock
    private UrlFileDownloader urlFileDownloader;

    @Test
    void backUpFileShouldDownloadFile() throws IOException {
        GameFile gameFile = discoveredGameFile().build();
        String tempFilePath = "someTempFilePath";
        BackupProgress backupProgress = mock(BackupProgress.class);

        gogFileBackupService.backUpFile(gameFile, tempFilePath, backupProgress);

        verify(urlFileDownloader).downloadFile(gogEmbedClient, gameFile, tempFilePath, backupProgress);
    }

    @Test
    void backUpFileShouldReturnFilePath() throws IOException {
        GameFile gameFile = discoveredGameFile().build();
        var tempFilePath = "someTempFilePath";
        BackupProgress backupProgress = mock(BackupProgress.class);
        String finalFilePath = mockSuccessfulFileDownload(gameFile, tempFilePath, backupProgress);

        String result = gogFileBackupService.backUpFile(gameFile, tempFilePath, backupProgress);

        assertThat(result).isEqualTo(finalFilePath);
    }

    private String mockSuccessfulFileDownload(GameFile gameFile, String tempFilePath, BackupProgress backupProgress)
            throws IOException {
        var finalFilePath = "finalFilePath";
        when(urlFileDownloader.downloadFile(gogEmbedClient, gameFile, tempFilePath, backupProgress))
                .thenReturn(finalFilePath);
        return finalFilePath;
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