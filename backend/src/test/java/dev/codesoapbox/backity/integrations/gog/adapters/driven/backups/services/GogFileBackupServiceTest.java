package dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GogFileBackupServiceTest {

    @InjectMocks
    private GogFileBackupServiceGame gogFileDownloader;

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

        gogFileDownloader.backUpFile(gameFile, tempFilePath);

        verify(urlFileDownloader).downloadFile(gogEmbedClient, gameFile, tempFilePath);
    }

    @Test
    void backUpFileShouldReturnFilePath() throws IOException {
        GameFile gameFile = discoveredGameFile().build();
        String tempFilePath = "someTempFilePath";
        String finalFilePath = "finalFilePath";
        when(urlFileDownloader.downloadFile(gogEmbedClient, gameFile, tempFilePath))
                .thenReturn(finalFilePath);

        String result = gogFileDownloader.backUpFile(gameFile, tempFilePath);

        assertThat(result).isEqualTo(finalFilePath);
    }

    @Test
    void isReadyShouldReturnTrueIfReady() {
        when(authService.isAuthenticated())
                .thenReturn(false)
                .thenReturn(true);

        assertThat(gogFileDownloader.isReady()).isFalse();
        assertThat(gogFileDownloader.isReady()).isTrue();
    }

    @Test
    void shouldGetGameProviderId() {
        assertThat(gogFileDownloader.getGameProviderId()).isEqualTo(new GameProviderId("GOG"));
    }
}