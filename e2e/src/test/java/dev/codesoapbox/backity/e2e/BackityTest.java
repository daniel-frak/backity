package dev.codesoapbox.backity.e2e;

import com.microsoft.playwright.Download;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import dev.codesoapbox.backity.e2e.backend.BackupTargetsApi;
import dev.codesoapbox.backity.e2e.backend.FileCopiesApi;
import dev.codesoapbox.backity.e2e.backend.FileCopyQueueApi;
import dev.codesoapbox.backity.e2e.backend.GamesApi;
import dev.codesoapbox.backity.e2e.pages.GameProvidersPage;
import dev.codesoapbox.backity.e2e.pages.GamesPage;
import dev.codesoapbox.backity.e2e.pages.SettingsPage;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UsePlaywright(CustomOptions.class)
class BackityTest {

    private static final String FILE_TO_DOWNLOAD_TITLE = "Test Game 1 Installer (Part 1 of 3)";
    private static final String FILE_TO_DOWNLOAD_NAME = "test_game_1_installer_1.exe";
    private static final String FILE_TO_DOWNLOAD_EXPECTED_CONTENTS = "Source file contents";
    private static final String LOCAL_FOLDER_BACKUP_TARGET_NAME = "Local folder";

    private GameProvidersPage gameProvidersPage;
    private GamesPage gamesPage;
    private SettingsPage settingsPage;

    @BeforeEach
    void setUp(Page page) {
        this.gameProvidersPage = new GameProvidersPage(page);
        this.gamesPage = new GamesPage(page, new GamesApi(), new FileCopiesApi(), new FileCopyQueueApi());
        this.settingsPage = new SettingsPage(page, new BackupTargetsApi());
        resetState();
    }

    private void resetState() {
        tryToLogOutOfGog();
        deleteAllFileBackups();
        deleteAllBackupTargets();
    }

    private void tryToLogOutOfGog() {
        gameProvidersPage.visit();
        if (gameProvidersPage.isGogAuthenticated()) {
            gameProvidersPage.logOutFromGog();
        }
    }

    private void deleteAllFileBackups() {
        gamesPage.visit();
        gamesPage.deleteAllFileCopies();
    }

    private void deleteAllBackupTargets() {
        settingsPage.visit();
        settingsPage.deleteAllBackupTargets();
    }

    @AfterEach
    void tearDown() {
        resetState();
    }

    @Test
    void shouldBackupGogFiles() {
        settingsPage.visit();
        settingsPage.createBackupTarget(LOCAL_FOLDER_BACKUP_TARGET_NAME);

        gameProvidersPage.visit();
        gameProvidersPage.authenticateGog();
        gameProvidersPage.discoverAllFiles();

        gamesPage.visit();
        gamesPage.backUpFile(FILE_TO_DOWNLOAD_TITLE, LOCAL_FOLDER_BACKUP_TARGET_NAME);

        assertFileCopyIsBackedUp();
        Download download = gamesPage.startFileDownload(FILE_TO_DOWNLOAD_TITLE, LOCAL_FOLDER_BACKUP_TARGET_NAME);
        try {
            assertFileNameIsCorrect(download);
            String fileContent = downloadFileAndReadContent(download);
            assertFileContentIsCorrect(fileContent);
        } finally {
            download.delete();
        }
    }

    private void assertFileCopyIsBackedUp() {
        assertTrue(
                gamesPage.fileCopyStatusIsStoredIntegrityUnknown(
                        FILE_TO_DOWNLOAD_TITLE, LOCAL_FOLDER_BACKUP_TARGET_NAME),
                () -> "Expected File Copy status to be Stored Integrity Unknown, but was: "
                        + gamesPage.getFileCopyStatusText(FILE_TO_DOWNLOAD_TITLE, LOCAL_FOLDER_BACKUP_TARGET_NAME));
    }

    private void assertFileNameIsCorrect(Download download) {
        assertEquals(FILE_TO_DOWNLOAD_NAME, download.suggestedFilename(),
                () -> "Expected file name to be " + FILE_TO_DOWNLOAD_NAME
                        + ", but was " + download.suggestedFilename());
    }

    @SneakyThrows
    private String downloadFileAndReadContent(Download download) {
        try (
                InputStream inputStream = download.createReadStream();
                var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        ) {
            return reader.lines()
                    .collect(Collectors.joining("\n"));
        }
    }

    private void assertFileContentIsCorrect(String fileContent) {
        assertEquals(FILE_TO_DOWNLOAD_EXPECTED_CONTENTS, fileContent,
                () -> "Expected file content to be " + FILE_TO_DOWNLOAD_EXPECTED_CONTENTS
                        + ", but was " + fileContent);
    }
}
