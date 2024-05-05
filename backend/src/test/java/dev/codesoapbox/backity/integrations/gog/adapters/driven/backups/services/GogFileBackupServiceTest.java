package dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services;

import dev.codesoapbox.backity.core.backup.domain.FileSourceId;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services.embed.GogEmbedWebClient;
import dev.codesoapbox.backity.integrations.gog.domain.services.GogAuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static dev.codesoapbox.backity.core.filedetails.domain.TestFileDetails.discoveredFileDetails;
import static org.assertj.core.api.Assertions.assertThat;
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
    void backUpFileShouldDownloadFile() throws IOException {
        FileDetails fileDetails = discoveredFileDetails().build();
        String tempFilePath = "someTempFilePath";

        gogFileDownloader.backUpFile(fileDetails, tempFilePath);

        verify(urlFileDownloader).downloadFile(gogEmbedClient, fileDetails, tempFilePath);
    }

    @Test
    void backUpFileShouldReturnFilePath() throws IOException {
        FileDetails fileDetails = discoveredFileDetails().build();
        String tempFilePath = "someTempFilePath";
        String finalFilePath = "finalFilePath";
        when(urlFileDownloader.downloadFile(gogEmbedClient, fileDetails, tempFilePath))
                .thenReturn(finalFilePath);

        String result = gogFileDownloader.backUpFile(fileDetails, tempFilePath);

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
    void shouldGetSource() {
        assertThat(gogFileDownloader.getSource()).isEqualTo(new FileSourceId("GOG"));
    }
}