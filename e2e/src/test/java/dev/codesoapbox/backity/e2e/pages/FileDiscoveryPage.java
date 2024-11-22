package dev.codesoapbox.backity.e2e.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import dev.codesoapbox.backity.e2e.actions.Repeat;
import lombok.Getter;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class FileDiscoveryPage {
    private static final String GAME_FILES_DISCOVERED_URL = "/game-files?processing-status=discovered";
    private static final String GAME_FILES_ENQUEUE_URL = "/game-file-actions/enqueue";

    private final Page page;
    private final Locator discoverNewFilesBtn;
    private final Locator fileDiscoveryStatusBadge;
    private final Locator refreshDiscoveredFilesBtn;

    @Getter
    private final Locator discoveredFilesTable;

    public FileDiscoveryPage(Page page) {
        this.page = page;
        discoverNewFilesBtn = page.getByTestId("discover-new-files-btn");
        fileDiscoveryStatusBadge = page.getByTestId("file-discovery-status-badge");
        refreshDiscoveredFilesBtn = page.getByTestId("refresh-discovered-files-btn");
        discoveredFilesTable = page.getByTestId("discovered-files-table");
    }

    public void navigate() {
        page.navigate("/file-discovery");
    }

    public void discoverNewFiles() {
        discoverNewFilesBtn.click();
        waitUntilFileDiscoveryIsFinished();
        refreshDiscoveredFilesBtn.click();
    }

    private void waitUntilFileDiscoveryIsFinished() {
        assertThat(fileDiscoveryStatusBadge).not().containsText("Discovery in progress");
    }

    public Locator getDiscoveredFileRow(String gameProviderIdGameTitle) {
        refreshDiscoveredFilesUntilTheyContain(gameProviderIdGameTitle);
        return discoveredFilesTable.locator("tr")
                .filter(new Locator.FilterOptions().setHasText(gameProviderIdGameTitle));
    }

    private void refreshDiscoveredFilesUntilTheyContain(String gameProviderIdGameTitle) {
        Repeat.on(page)
                .action(refreshDiscoveredFilesBtn::click)
                .expectingResponse(response ->
                        response.url().contains(GAME_FILES_DISCOVERED_URL) && response.status() == 200)
                .until(() -> discoveredFilesTable.textContent().contains(gameProviderIdGameTitle));
    }

    public void backUpFile(String gameProviderIdGameTitle) {
        refreshDiscoveredFilesUntilTheyContain(gameProviderIdGameTitle);
        Locator gameTitleRow = discoveredFilesTable.locator("tr")
                .filter(new Locator.FilterOptions().setHasText(gameProviderIdGameTitle));
        Locator backupFileBtn = gameTitleRow.getByTestId("back-up-btn");
        page.waitForResponse(response -> response.url().contains(GAME_FILES_ENQUEUE_URL)
                                         && response.status() == 200,
                backupFileBtn::click);
    }
}
