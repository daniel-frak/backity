package dev.codesoapbox.backity.e2e.pages;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.ElementState;
import com.microsoft.playwright.options.WaitForSelectorState;
import dev.codesoapbox.backity.e2e.actions.Repeat;

public class GamesPage {

    private static final String GAMES_URL = "/games";
    private static final String FILE_COPY_URL = "/file-copies";
    private static final String FILE_COPY_QUEUE_URL = "/file-copy-queue";
    private static final String DOWNLOAD_FILE_BACKUP_BTN_TEST_ID = "download-file-copy-btn";
    private static final String BACKUP_FILE_COPY_BTN_TEST_ID = "backup-file-btn";
    private static final String GAME_FILE_TEST_ID = "game-file-item";
    private static final String FILE_COPY_ITEM_TEST_ID = "file-copy-item";
    private static final String FILE_COPY_STATUS_TEST_ID = "file-copy-status";

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
        page.waitForResponse(this::isSuccessfulGetGamesResponse, refreshGamesButton::click);
        loader.first().waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.HIDDEN));
    }

    private boolean isSuccessfulGetGamesResponse(Response response) {
        return response.url().contains(GAMES_URL) && isSuccessful(response);
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

    public void backUpFile(String fileTitle, String backupTargetName) {
        Locator fileCopyBackupBtn = getFileCopyBackupButton(fileTitle, backupTargetName);
        page.waitForResponse(this::isSuccessfulFileCopyEnqueueResponse,
                fileCopyBackupBtn::click);
    }

    private boolean isSuccessfulFileCopyEnqueueResponse(Response response) {
        return response.url().contains(FILE_COPY_QUEUE_URL)
                && response.request().method().equals("POST")
                && isSuccessful(response);
    }

    private boolean isSuccessful(Response response) {
        return response.status() >= 200 && response.status() < 300;
    }

    private Locator getFileCopyBackupButton(String fileTitle, String backupTargetName) {
        return getFileCopyItem(fileTitle, backupTargetName)
                .getByTestId(BACKUP_FILE_COPY_BTN_TEST_ID);
    }

    private Locator getFileCopyItem(String fileTitle, String backupTargetName) {
        return gameList
                .getByTestId(GAME_FILE_TEST_ID)
                .filter(new Locator.FilterOptions().setHasText(fileTitle))
                .getByTestId(FILE_COPY_ITEM_TEST_ID)
                .filter(new Locator.FilterOptions().setHasText(backupTargetName));
    }

    public Locator getFileCopyStatus(String fileTitle, String backupTargetName) {
        return getFileCopyItem(fileTitle, backupTargetName)
                .getByTestId(FILE_COPY_STATUS_TEST_ID);
    }

    public Download startFileDownload(String fileTitle, String backupTargetName) {
        Locator fileCopyDownloadButton = getFileCopyDownloadButton(fileTitle, backupTargetName);
        return page.waitForDownload(fileCopyDownloadButton::click);
    }

    private Locator getFileCopyDownloadButton(String fileTitle, String backupTargetName) {
        return getFileCopyItem(fileTitle, backupTargetName)
                .getByTestId(DOWNLOAD_FILE_BACKUP_BTN_TEST_ID);
    }
}
