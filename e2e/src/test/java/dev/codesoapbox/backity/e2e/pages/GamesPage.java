package dev.codesoapbox.backity.e2e.pages;

import com.microsoft.playwright.Download;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import dev.codesoapbox.backity.e2e.actions.Repeat;

public class GamesPage {

    private static final String GAMES_URL = "/games";
    private static final String FILE_BACKUP_URL = "/file-backup";
    private static final String DOWNLOAD_FILE_BACKUP_BTN_TEST_ID = "download-file-backup-btn";

    private final Page page;
    private final Locator loader;
    private final Locator refreshGamesButton;
    private final Locator deleteFileBackupButtons;
    private final Locator confirmFileBackupDeleteButton;
    private final Locator gameList;

    public GamesPage(final Page page) {
        this.page = page;
        this.loader = page.getByTestId("loader");
        this.refreshGamesButton = page.getByTestId("refresh-games-btn");
        this.deleteFileBackupButtons = page.getByTestId("delete-file-backup-btn");
        this.confirmFileBackupDeleteButton = page.getByTestId("confirmation-modal-yes-btn");
        this.gameList = page.getByTestId("game-list");
    }

    public void visit() {
        page.navigate("/games");
    }

    public void deleteAllFileBackups() {
        refreshGames();
        deleteAllFileBackupsOneByOne();
    }

    private void refreshGames() {
        page.waitForResponse(response -> response.url().contains(GAMES_URL) && response.status() == 200,
                refreshGamesButton::click);
        loader.first().waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.HIDDEN));
    }

    private void deleteAllFileBackupsOneByOne() {
        Repeat.on(page)
                .action(() -> {
                    deleteFileBackupButtons.first().click();
                    confirmFileBackupDeleteButton.click();
                })
                .expectingResponse(response -> response.url().contains(FILE_BACKUP_URL)
                                               && response.request().method().equals("DELETE")
                                               && response.status() == 204)
                .until(() -> !loader.first().isVisible() && !deleteFileBackupButtons.first().isVisible());
    }

    public Download startFileDownload(String fileTitle) {
        Locator downloadFileBackupButton = getDownloadFileBackupButton(fileTitle);
        return page.waitForDownload(downloadFileBackupButton::click);
    }

    private Locator getDownloadFileBackupButton(String fileTitle) {
        return gameList
                .filter(new Locator.FilterOptions().setHasText(fileTitle))
                .getByTestId(DOWNLOAD_FILE_BACKUP_BTN_TEST_ID);
    }
}
