package dev.codesoapbox.backity.e2e.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import dev.codesoapbox.backity.e2e.actions.Repeat;
import lombok.Getter;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class FileDiscoveryPage {

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

    public Locator getDiscoveredFileRow(String sourceGameTitle) {
        refreshDiscoveredFilesUntilTheyContain(sourceGameTitle);
        return discoveredFilesTable.locator("tr")
                .filter(new Locator.FilterOptions().setHasText(sourceGameTitle));
    }

    private void refreshDiscoveredFilesUntilTheyContain(String sourceGameTitle) {
        Repeat.on(page)
                .action(refreshDiscoveredFilesBtn::click)
                .expectingResponse(response ->
                        response.url().contains("/file-details/discovered") && response.status() == 200)
                .until(() -> discoveredFilesTable.textContent().contains(sourceGameTitle));
    }

    public void backUpFile(String sourceGameTitle) {
        refreshDiscoveredFilesUntilTheyContain(sourceGameTitle);
        Locator gameTitleRow = discoveredFilesTable.locator("tr")
                .filter(new Locator.FilterOptions().setHasText(sourceGameTitle));
        Locator backupFileBtn = gameTitleRow.getByTestId("back-up-btn");
        page.waitForResponse(response -> response.url().contains("/file-details/enqueue")
                                         && response.status() == 200,
                backupFileBtn::click);
    }
}
