package dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services;

import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetailsId;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services.embed.GogEmbedWebClient;
import dev.codesoapbox.backity.integrations.gog.domain.services.GogAuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GogFileBackupServiceTest {

    @InjectMocks
    private GogFileBackupService gogFileDownloader;

    @Mock
    private GogEmbedWebClient gogEmbedClient;

    @Mock
    private GogAuthService authService;

    @Mock
    private UrlFileDownloader urlFileDownloader;

    @Test
    void shouldDownloadGameFile() throws IOException {
        var gameFileVersionBackup = new GameFileDetails(
                new GameFileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48")),
                "someSource", "someUrl", "someTitle", "someOriginalFileName",
                null, null, "someGameId", "someVersion", "100 KB", null,
                null, FileBackupStatus.DISCOVERED, null);
        String tempFilePath = "someTempFilePath";

        gogFileDownloader.backUpGameFile(gameFileVersionBackup, tempFilePath);

        verify(urlFileDownloader).downloadGameFile(gogEmbedClient, gameFileVersionBackup, tempFilePath);
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