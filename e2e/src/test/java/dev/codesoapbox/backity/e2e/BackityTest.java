package dev.codesoapbox.backity.e2e;

import com.microsoft.playwright.Download;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import dev.codesoapbox.backity.e2e.pages.AuthenticationPage;
import dev.codesoapbox.backity.e2e.pages.FileBackupPage;
import dev.codesoapbox.backity.e2e.pages.GameContentDiscoveryPage;
import dev.codesoapbox.backity.e2e.pages.GamesPage;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.stream.Collectors;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@UsePlaywright(CustomOptions.class)
class BackityTest {

    private static final String FILE_TO_DOWNLOAD_TITLE = "Test Game 1 Installer (Part 1 of 3)";
    private static final String FILE_TO_DOWNLOAD_NAME = "test_game_1_installer_1.exe";
    private static final String FILE_TO_DOWNLOAD_EXPECTED_CONTENTS = "Game file contents";

    private AuthenticationPage authenticationPage;
    private GameContentDiscoveryPage gameContentDiscoveryPage;
    private FileBackupPage fileBackupPage;
    private GamesPage gamesPage;

    @BeforeEach
    void setUp(Page page) {
        this.authenticationPage = new AuthenticationPage(page);
        this.gameContentDiscoveryPage = new GameContentDiscoveryPage(page);
        this.fileBackupPage = new FileBackupPage(page);
        this.gamesPage = new GamesPage(page);
        resetState();
    }

    private void resetState() {
        tryToLogOutOfGog();
        deleteAllFileBackups();
    }

    private void tryToLogOutOfGog() {
        authenticationPage.navigate();
        if (authenticationPage.isAuthenticated()) {
            authenticationPage.logOut();
        }
        assertFalse(authenticationPage.isAuthenticated());
    }

    private void deleteAllFileBackups() {
        gamesPage.visit();
        gamesPage.deleteAllFileCopies();
    }

    @AfterEach
    void tearDown() {
        resetState();
    }

    @Test
    void shouldBackupGogFiles() {
        authenticateGog();

        gameContentDiscoveryPage.navigate();
        discoverNewFiles(gameContentDiscoveryPage);
        gameContentDiscoveryPage.backUpFile(FILE_TO_DOWNLOAD_TITLE);
        assertThatFileWasDownloaded(FILE_TO_DOWNLOAD_TITLE);

        gamesPage.visit();
        Download download = gamesPage.startFileDownload(FILE_TO_DOWNLOAD_TITLE);
        assertEquals(FILE_TO_DOWNLOAD_NAME, download.suggestedFilename());
        String fileContent = downloadFileAndReadContent(download.url());
        assertEquals(FILE_TO_DOWNLOAD_EXPECTED_CONTENTS, fileContent);
    }

    @SneakyThrows
    private String downloadFileAndReadContent(String downloadUrl) {
        URL url = new URI(downloadUrl).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try (InputStream inputStream = connection.getInputStream();
             var reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private void authenticateGog() {
        authenticationPage.navigate();
        authenticationPage.authenticateGog();
        assertTrue(authenticationPage.isAuthenticated());
    }

    private void discoverNewFiles(GameContentDiscoveryPage gameContentDiscoveryPage) {
        gameContentDiscoveryPage.startDiscovery();
        assertThat(gameContentDiscoveryPage.getDiscoveredFileRow(FILE_TO_DOWNLOAD_TITLE)).isVisible();
    }

    private void assertThatFileWasDownloaded(String fileTitle) {
        fileBackupPage.navigate();
        Locator processedFileStatus = fileBackupPage.getProcessedFilesGameTitleStatus(fileTitle);
        assertThat(processedFileStatus).isVisible();
        assertThat(processedFileStatus).containsText("SUCCESS");
    }
}
