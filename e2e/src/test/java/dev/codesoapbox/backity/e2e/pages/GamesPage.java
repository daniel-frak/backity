package dev.codesoapbox.backity.e2e.pages;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.ElementState;
import com.microsoft.playwright.options.WaitForSelectorState;
import dev.codesoapbox.backity.e2e.actions.Repeat;

public class GamesPage {

    private static final String GAMES_URL = "/games";
    private static final String FILE_COPY_URL = "/file-copies";
    private static final String DOWNLOAD_FILE_BACKUP_BTN_TEST_ID = "download-file-copy-btn";
    private static final String BACKUP_FILE_COPY_BTN_TEST_ID = "backup-file-btn";
    private static final String FILE_COPY_ENQUEUE_URL = "/file-copy-actions/enqueue";
    private static final String FILE_COPY_ELEMENT = "tr";

    private final Page page;
    private final Locator loader;
    private final Locator refreshGamesButton;
    private final Locator deleteFileCopyButtons;
    private final Locator confirmFileCopyDeleteButton;
    private final Locator gameList;

    public GamesPage(final Page page) {
        this.page = page;
        this.loader = page.getByTestId("loader");
        this.refreshGamesButton = page.getByTestId("refresh-games-btn");
        this.deleteFileCopyButtons = page.getByTestId("delete-file-copy-btn");
        this.confirmFileCopyDeleteButton = page.getByTestId("confirmation-modal-yes-btn");
        this.gameList = page.getByTestId("game-list");
    }

    public void visit() {
        page.navigate("/games");
    }

    public void deleteAllFileCopies() {
        refreshGames();
        deleteAllFileCopiesOneByOne();
    }

    private void refreshGames() {
        page.waitForResponse(response -> response.url().contains(GAMES_URL) && response.status() == 200,
                refreshGamesButton::click);
        loader.first().waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.HIDDEN));
    }

    private void deleteAllFileCopiesOneByOne() {
        Repeat.on(page)
                .action(() -> {
                    ElementHandle currentDeleteButton = deleteFileCopyButtons.first().elementHandle();
                    currentDeleteButton.click();
                    confirmFileCopyDeleteButton.click();
                    loader.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
                    currentDeleteButton.waitForElementState(ElementState.HIDDEN);
                })
                .expectingResponse(this::deleteApiResponseIsSuccessful)
                .until(() -> deleteFileCopyButtons.count() == 0);
    }

    private boolean deleteApiResponseIsSuccessful(Response response) {
        return response.url().contains(FILE_COPY_URL)
               && response.request().method().equals("DELETE")
               && response.status() == 204;
    }

    public void backUpFile(String fileTitle) {
        Locator backupFileBtn = getBackupFileCopyBackupButton(fileTitle);
        page.waitForResponse(response -> response.url().contains(FILE_COPY_ENQUEUE_URL)
                                         && response.status() == 200,
                backupFileBtn::click);
    }

    private Locator getBackupFileCopyBackupButton(String fileTitle) {
        return gameList
                .locator(FILE_COPY_ELEMENT)
                .filter(new Locator.FilterOptions().setHasText(fileTitle))
                .getByTestId(BACKUP_FILE_COPY_BTN_TEST_ID);
    }

    public Locator getFileCopyStatus(String fileTitle) {
        return gameList
                .locator(FILE_COPY_ELEMENT)
                .filter(new Locator.FilterOptions().setHasText(fileTitle))
                .locator("[data-title=\"Status\"]");
    }

    public Download startFileDownload(String fileTitle) {
        Locator downloadFileBackupButton = getDownloadFileCopyButton(fileTitle);
        return page.waitForDownload(downloadFileBackupButton::click);
    }

    private Locator getDownloadFileCopyButton(String fileTitle) {
        return gameList
                .locator(FILE_COPY_ELEMENT)
                .filter(new Locator.FilterOptions().setHasText(fileTitle))
                .getByTestId(DOWNLOAD_FILE_BACKUP_BTN_TEST_ID);
    }
}
