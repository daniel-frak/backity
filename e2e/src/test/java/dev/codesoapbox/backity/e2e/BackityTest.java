package dev.codesoapbox.backity.e2e;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import dev.codesoapbox.backity.e2e.pages.AuthenticationPage;
import dev.codesoapbox.backity.e2e.pages.FileBackupPage;
import dev.codesoapbox.backity.e2e.pages.FileDiscoveryPage;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@UsePlaywright(CustomOptions.class)
class BackityTest {

    private static final String FILE_TO_DOWNLOAD_GAME_TITLE = "test_game_1_installer_1";

    @Test
    void shouldBackupGogFiles(Page page) {
        authenticateGog(page);

        var fileDiscoveryPage = new FileDiscoveryPage(page);
        fileDiscoveryPage.navigate();
        discoverNewFiles(fileDiscoveryPage);
        fileDiscoveryPage.backUpFile(FILE_TO_DOWNLOAD_GAME_TITLE);
        assertThatFileWasDownloaded(page);
    }

    private void authenticateGog(Page page) {
        var authenticationPage = new AuthenticationPage(page);
        authenticationPage.navigate();

        if (authenticationPage.getGogAuthBadge().textContent().contains("Authenticated")) {
            return;
        }
        authenticationPage.authenticateGog();

        assertThat(authenticationPage.getGogAuthBadge()).containsText("Authenticated");
    }

    private void discoverNewFiles(FileDiscoveryPage fileDiscoveryPage) {
        fileDiscoveryPage.discoverNewFiles();
        assertThat(fileDiscoveryPage.getDiscoveredFileRow(FILE_TO_DOWNLOAD_GAME_TITLE)).isVisible();
    }

    private void assertThatFileWasDownloaded(Page page) {
        var fileBackupPage = new FileBackupPage(page);
        fileBackupPage.navigate();
        Locator processedFileStatus = fileBackupPage.getProcessedFilesGameTitleStatus(FILE_TO_DOWNLOAD_GAME_TITLE);
        assertThat(processedFileStatus).containsText("SUCCESS");
    }
}
