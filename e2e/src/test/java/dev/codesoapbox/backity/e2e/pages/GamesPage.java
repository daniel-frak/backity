package dev.codesoapbox.backity.e2e.pages;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import dev.codesoapbox.backity.e2e.actions.Repeat;

public class GamesPage {

    private static final String GAMES_URL = "/games";
    private static final String FILE_COPY_URL = "/file-copy";
    private static final String DOWNLOAD_FILE_BACKUP_BTN_TEST_ID = "download-file-backup-btn";

    private final Page page;
    private final Locator loader;
    private final Locator refreshGamesButton;
    private final Locator deleteFileCopyButtons;
    private final Locator confirmFileBackupDeleteButton;
    private final Locator gameList;

    public GamesPage(final Page page) {
        this.page = page;
        this.loader = page.getByTestId("loader");
        this.refreshGamesButton = page.getByTestId("refresh-games-btn");
        this.deleteFileCopyButtons = page.getByTestId("delete-file-copy-btn");
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
                    Locator currentDeleteButton = deleteFileCopyButtons.first();
                    currentDeleteButton.click();
                    confirmFileBackupDeleteButton.click();
                    loader.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
                    currentDeleteButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
                })
                .expectingResponse(this::deleteApiResponseIsSuccessful)
                .until(() -> deleteFileCopyButtons.count() == 0);
    }

    private boolean deleteApiResponseIsSuccessful(Response response) {
        return response.url().contains(FILE_COPY_URL)
               && response.request().method().equals("DELETE")
               && response.status() == 204;
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
