package dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.services;

import dev.codesoapbox.backity.core.files.domain.downloading.model.GameFileVersion;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.services.embed.GogEmbedWebClient;
import dev.codesoapbox.backity.integrations.gog.domain.services.GogAuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GogFileDownloaderTest {

    @InjectMocks
    private GogFileDownloader gogFileDownloader;

    @Mock
    private GogEmbedWebClient gogEmbedClient;

    @Mock
    private GogAuthService authService;

    @Mock
    private UrlFileDownloader urlFileDownloader;

    @Test
    void shouldDownloadGameFile() throws IOException {
        GameFileVersion gameFileVersion = GameFileVersion.builder()
                .gameTitle("some game")
                .url("someUrl")
                .build();
        String tempFilePath = "someTempFilePath";

        gogFileDownloader.downloadGameFile(gameFileVersion, tempFilePath);

        verify(urlFileDownloader).downloadGameFile(gogEmbedClient, gameFileVersion.getUrl(), tempFilePath);
    }

    @Test
    void isReadyShouldReturnTrueIfReady() {
        when(authService.isAuthenticated())
                .thenReturn(false)
                .thenReturn(true);

        assertFalse(gogFileDownloader.isReady());
        assertTrue(gogFileDownloader.isReady());
    }

    @Test
    void shouldGetSource() {
        assertEquals("GOG", gogFileDownloader.getSource());
    }
}