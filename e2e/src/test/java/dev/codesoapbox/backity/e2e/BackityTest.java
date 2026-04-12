package dev.codesoapbox.backity.e2e;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import dev.codesoapbox.backity.e2e.actions.InMemoryDownload;
import dev.codesoapbox.backity.e2e.backend.BackupTargetsApi;
import dev.codesoapbox.backity.e2e.backend.FileCopiesApi;
import dev.codesoapbox.backity.e2e.backend.FileCopyQueueApi;
import dev.codesoapbox.backity.e2e.backend.GamesApi;
import dev.codesoapbox.backity.e2e.pages.GameProvidersPage;
import dev.codesoapbox.backity.e2e.pages.GamesPage;
import dev.codesoapbox.backity.e2e.pages.SettingsPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UsePlaywright(CustomOptions.class)
class BackityTest {

    private static final String FILE_TO_DOWNLOAD_TITLE = "Test Game 1 Installer (Part 1 of 3)";
    private static final String FILE_TO_DOWNLOAD_NAME = "test_game_1_installer_1.exe";
    private static final String FILE_TO_DOWNLOAD_EXPECTED_CONTENT = "Source file contents";
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
        createBackupTarget();
        performInitialFileDiscovery();

        backupFile(FILE_TO_DOWNLOAD_TITLE, LOCAL_FOLDER_BACKUP_TARGET_NAME);

        assertFileCopyIsBackedUp();
        InMemoryDownload download =
                gamesPage.startFileDownload(FILE_TO_DOWNLOAD_TITLE, LOCAL_FOLDER_BACKUP_TARGET_NAME);
        assertFileNameIs(download, FILE_TO_DOWNLOAD_NAME);
        assertFileContentIs(download, FILE_TO_DOWNLOAD_EXPECTED_CONTENT);
    }

    private void createBackupTarget() {
        settingsPage.visit();
        settingsPage.createBackupTarget(BackityTest.LOCAL_FOLDER_BACKUP_TARGET_NAME);
    }

    private void performInitialFileDiscovery() {
        gameProvidersPage.visit();
        gameProvidersPage.authenticateGog();
        gameProvidersPage.discoverAllFiles();
    }

    @SuppressWarnings("SameParameterValue")
    private void backupFile(String fileTitle, String backupTargetName) {
        gamesPage.visit();
        gamesPage.backUpFile(fileTitle, backupTargetName);
    }

    private void assertFileCopyIsBackedUp() {
        assertTrue(
                gamesPage.fileCopyStatusIsStoredIntegrityUnknown(
                        FILE_TO_DOWNLOAD_TITLE, LOCAL_FOLDER_BACKUP_TARGET_NAME),
                () -> "Expected File Copy status to be Stored Integrity Unknown, but was: "
                        + gamesPage.getFileCopyStatusText(FILE_TO_DOWNLOAD_TITLE, LOCAL_FOLDER_BACKUP_TARGET_NAME));
    }

    @SuppressWarnings("SameParameterValue")
    private void assertFileNameIs(InMemoryDownload download, String expectedFileName) {
        String actualFileName = download.suggestedFilename();

        assertEquals(
                expectedFileName,
                actualFileName,
                () -> "Expected file name to be %s, but was %s".formatted(expectedFileName, actualFileName)
        );
    }

    @SuppressWarnings("SameParameterValue")
    private void assertFileContentIs(InMemoryDownload download, String expectedFileContent) {
        String fileContent = download.downloadFileAndReadContent();

        assertEquals(
                expectedFileContent,
                fileContent,
                () -> "Expected file content to be %s, but was %s".formatted(expectedFileContent, fileContent)
        );
    }
}
