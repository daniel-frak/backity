package dev.codesoapbox.backity.e2e.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import dev.codesoapbox.backity.e2e.actions.Repeat;

public class FileBackupPage {

    private static final String GAME_FILES_PROCESSED_URL = "/game-files?processing-status=processed";
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

    public Locator getProcessedFilesGameTitleStatus(String gameProviderIdGameTitle) {
        refreshQueueUntilItContains(gameProviderIdGameTitle);
        Locator gameTitleRow = processedFilesTable.locator("tr")
                .filter(new Locator.FilterOptions().setHasText(gameProviderIdGameTitle));
        return gameTitleRow.locator("[data-title=\"Status\"]");
    }

    private void refreshQueueUntilItContains(String gameProviderIdGameTitle) {
        Repeat.on(page)
                .action(refreshQueueButton::click)
                .expectingResponse(response ->
                        response.url().contains(GAME_FILES_PROCESSED_URL) && response.status() == 200)
                .until(() -> processedFilesTable.textContent().contains(gameProviderIdGameTitle));
    }
}
