package dev.codesoapbox.backity.e2e.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import dev.codesoapbox.backity.e2e.actions.Repeat;

public class FileBackupPage {

    private static final String GAME_FILES_PROCESSED_URL = "/game-files?processing-status=processed";
    private final Page page;

    private final Locator refreshProcessedFilesButton;
    private final Locator processedFilesTable;

    public FileBackupPage(Page page) {
        this.page = page;
        refreshProcessedFilesButton = page.getByTestId("refresh-processed-files-btn");
        processedFilesTable = page.getByTestId("processed-files-table");
    }

    public void navigate() {
        page.navigate("/file-backup");
    }

    public Locator getProcessedFilesGameTitleStatus(String fileTitle) {
        refreshProcessedFilesUntilItContains(fileTitle);
        Locator gameTitleRow = processedFilesTable.locator("tr")
                .filter(new Locator.FilterOptions().setHasText(fileTitle));
        return gameTitleRow.locator("[data-title=\"Status\"]");
    }

    private void refreshProcessedFilesUntilItContains(String fileTitle) {
        Repeat.on(page)
                .action(refreshProcessedFilesButton::click)
                .expectingResponse(response ->
                        response.url().contains(GAME_FILES_PROCESSED_URL) && response.status() == 200)
                .until(() -> processedFilesTable.textContent().contains(fileTitle));
    }
}
