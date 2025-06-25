package dev.codesoapbox.backity.e2e;

import com.microsoft.playwright.Download;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.junit.UsePlaywright;
import dev.codesoapbox.backity.e2e.pages.GameProvidersPage;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@UsePlaywright(CustomOptions.class)
class BackityTest {

    private static final String FILE_TO_DOWNLOAD_TITLE = "Test Game 1 Installer (Part 1 of 3)";
    private static final String FILE_TO_DOWNLOAD_NAME = "test_game_1_installer_1.exe";
    private static final String FILE_TO_DOWNLOAD_EXPECTED_CONTENTS = "Game file contents";
    private static final String LOCAL_FOLDER_BACKUP_TARGET_NAME = "Local folder";

    // The file backup scheduler runs once every N seconds; We must make sure to not fail the test before then.
    private static final long EXPECTED_FILE_BACKUP_SCHEDULER_DELAY = 60_000L;

    private GameProvidersPage gameProvidersPage;
    private GamesPage gamesPage;

    @BeforeEach
    void setUp(Page page) {
        this.gameProvidersPage = new GameProvidersPage(page);
        this.gamesPage = new GamesPage(page);
        resetState();
    }

    private void resetState() {
        tryToLogOutOfGog();
        deleteAllFileBackups();
    }

    private void tryToLogOutOfGog() {
        gameProvidersPage.navigate();
        if (gameProvidersPage.isGogAuthenticated()) {
            gameProvidersPage.logOutFromGog();
        }
        assertIsNotAuthenticated();
    }

    private void assertIsNotAuthenticated() {
        assertThat(gameProvidersPage.getAuthenticationStatusLocator())
                .containsText(GameProvidersPage.NOT_AUTHENTICATED_LOWERCASE,
                        new LocatorAssertions.ContainsTextOptions().setIgnoreCase(true));
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
        gameProvidersPage.navigate();

        gameProvidersPage.authenticateGog();
        assertIsAuthenticated();

        gameProvidersPage.discoverAllFiles();

        gamesPage.visit();
        gamesPage.backUpFile(FILE_TO_DOWNLOAD_TITLE, LOCAL_FOLDER_BACKUP_TARGET_NAME);
        assertThatFileWasBackedUp(FILE_TO_DOWNLOAD_TITLE, LOCAL_FOLDER_BACKUP_TARGET_NAME);
        Download download = gamesPage.startFileDownload(FILE_TO_DOWNLOAD_TITLE, LOCAL_FOLDER_BACKUP_TARGET_NAME);
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

    private void assertIsAuthenticated() {
        assertThat(gameProvidersPage.getAuthenticationStatusLocator())
                .not().containsText(GameProvidersPage.NOT_AUTHENTICATED_LOWERCASE,
                        new LocatorAssertions.ContainsTextOptions().setIgnoreCase(true));
    }

    @SuppressWarnings("SameParameterValue")
    private void assertThatFileWasBackedUp(String fileTitle, String backupTargetName) {
        Locator processedFileStatus = gamesPage.getFileCopyStatus(fileTitle, backupTargetName);
        assertThat(processedFileStatus).isVisible();
        assertThat(processedFileStatus).containsText("STORED_INTEGRITY_UNKNOWN",
                new LocatorAssertions.ContainsTextOptions()
                        .setTimeout(EXPECTED_FILE_BACKUP_SCHEDULER_DELAY + 5000L));
    }
}
