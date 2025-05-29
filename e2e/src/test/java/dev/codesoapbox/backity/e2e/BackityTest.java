package dev.codesoapbox.backity.e2e;

import com.microsoft.playwright.Download;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.junit.UsePlaywright;
import dev.codesoapbox.backity.e2e.pages.AuthenticationPage;
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

    // The file backup scheduler runs once every N seconds
    private static final long EXPECTED_FILE_BACKUP_SCHEDULER_DELAY = 60_000L;

    private AuthenticationPage authenticationPage;
    private GameContentDiscoveryPage gameContentDiscoveryPage;
    private GamesPage gamesPage;

    @BeforeEach
    void setUp(Page page) {
        this.authenticationPage = new AuthenticationPage(page);
        this.gameContentDiscoveryPage = new GameContentDiscoveryPage(page);
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
        gameContentDiscoveryPage.discoverAllFiles();

        gamesPage.visit();
        gamesPage.backUpFile(FILE_TO_DOWNLOAD_TITLE);
        assertThatFileWasBackedUp(FILE_TO_DOWNLOAD_TITLE);
        Download download = gamesPage.startFileDownload(FILE_TO_DOWNLOAD_TITLE);
        try {
            assertEquals(FILE_TO_DOWNLOAD_NAME, download.suggestedFilename());
            String fileContent = downloadFileAndReadContent(download.url());
            assertEquals(FILE_TO_DOWNLOAD_EXPECTED_CONTENTS, fileContent);
        } finally {
            download.delete();
        }
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

    @SuppressWarnings("SameParameterValue")
    private void assertThatFileWasBackedUp(String fileTitle) {
        Locator processedFileStatus = gamesPage.getFileCopyStatus(fileTitle);
        assertThat(processedFileStatus).isVisible();
        assertThat(processedFileStatus).containsText("STORED_INTEGRITY_UNKNOWN",
                new LocatorAssertions.ContainsTextOptions()
                        .setTimeout(EXPECTED_FILE_BACKUP_SCHEDULER_DELAY + 5000L));
    }
}
