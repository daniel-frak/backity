package dev.codesoapbox.backity.e2e.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import dev.codesoapbox.backity.e2e.actions.Repeat;

public class FileBackupPage {

    private final Page page;

    private final Locator refreshQueueButton;
    private final Locator processedFilesTable;

    public FileBackupPage(Page page) {
        this.page = page;
        refreshQueueButton = page.getByTestId("refresh-queue-btn");
        processedFilesTable = page.getByTestId("processed-files-table");
    }

    public void navigate() {
        page.navigate("/file-backup");
    }

    public Locator getProcessedFilesGameTitleStatus(String sourceGameTitle) {
        refreshQueueUntilItContains(sourceGameTitle);
        Locator gameTitleRow = processedFilesTable.locator("tr")
                .filter(new Locator.FilterOptions().setHasText(sourceGameTitle));
        return gameTitleRow.locator("[data-title=\"Status\"]");
    }

    private void refreshQueueUntilItContains(String sourceGameTitle) {
        Repeat.on(page)
                .action(refreshQueueButton::click)
                .expectingResponse(response ->
                        response.url().contains("/file-details/processed") && response.status() == 200)
                .until(() -> processedFilesTable.textContent().contains(sourceGameTitle));
    }
}
