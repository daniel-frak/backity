package dev.codesoapbox.backity.e2e.pages;

import com.microsoft.playwright.Download;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import dev.codesoapbox.backity.e2e.actions.Repeat;
import dev.codesoapbox.backity.e2e.backend.FileCopiesApi;
import dev.codesoapbox.backity.e2e.backend.FileCopyQueueApi;
import dev.codesoapbox.backity.e2e.backend.GamesApi;

public class GamesPage {

    private static final Locator.ClickOptions SHORT_TIMEOUT_CLICK = new Locator.ClickOptions().setTimeout(2000);

    private static final String DOWNLOAD_FILE_BACKUP_BTN_TEST_ID = "download-file-copy-btn";
    private static final String BACKUP_FILE_COPY_BTN_TEST_ID = "backup-file-btn";
    private static final String CANCEL_FILE_BACKUP_BTN_TEST_ID = "cancel-file-backup-btn";
    private static final String SOURCE_FILE_TEST_ID = "game-file-item";
    private static final String FILE_COPY_ITEM_TEST_ID = "file-copy-item";
    private static final String FILE_COPY_STATUS_TEST_ID = "file-copy-status";

    private final Page page;
    private final GamesApi gamesApi;
    private final FileCopiesApi fileCopiesApi;
    private final FileCopyQueueApi fileCopyQueueApi;
    private final Locator loader;
    private final Locator searchButton;
    private final Locator deleteFileCopyButtons;
    private final Locator cancelFileCopyButtons;
    private final Locator confirmFileCopyDeleteButton;
    private final Locator gameList;

    public GamesPage(final Page page, final GamesApi gamesApi, final FileCopiesApi fileCopiesApi,
                     final FileCopyQueueApi fileCopyQueueApi) {
        this.page = page;
        this.gamesApi = gamesApi;
        this.fileCopiesApi = fileCopiesApi;
        this.fileCopyQueueApi = fileCopyQueueApi;
        this.loader = page.getByTestId("loader");
        this.searchButton = page.getByTestId("search-btn");
        this.deleteFileCopyButtons = page.getByTestId("delete-file-copy-btn");
        this.cancelFileCopyButtons = page.getByTestId(CANCEL_FILE_BACKUP_BTN_TEST_ID);
        this.confirmFileCopyDeleteButton = page.getByTestId("confirmation-modal-yes-btn");
        this.gameList = page.getByTestId("game-list");
    }

    public void visit() {
        page.navigate("/games");
    }

    public void deleteAllFileCopies() {
        refreshGames();
        deleteAllFileCopiesOneByOne();
        cancelAllBackupsOneByOne();
    }

    private void refreshGames() {
        page.waitForResponse(gamesApi::retrievedGames, searchButton::click);
        waitUntilLoaderDisappears();
    }

    private void deleteAllFileCopiesOneByOne() {
        Repeat.on(page)
                .action(() -> {
                    waitUntilLoaderDisappears();
                    Locator currentDeleteButton = deleteFileCopyButtons.first();
                    currentDeleteButton.click(SHORT_TIMEOUT_CLICK);
                    confirmFileCopyDeleteButton.click(SHORT_TIMEOUT_CLICK);
                })
                .expectingResponse(fileCopiesApi::isDeleted)
                .until(() -> {
                    waitUntilLoaderDisappears();
                    return deleteFileCopyButtons.count() == 0;
                });
    }

    private void cancelAllBackupsOneByOne() {
        Repeat.on(page)
                .action(() -> {
                    waitUntilLoaderDisappears();
                    Locator currentCancelButton = cancelFileCopyButtons.first();
                    currentCancelButton.click();
                    waitUntilLoaderDisappears();
                    currentCancelButton.waitFor(isHidden());
                })
                .expectingResponse(fileCopyQueueApi::isCanceled)
                .until(() -> {
                    waitUntilLoaderDisappears();
                    return cancelFileCopyButtons.count() == 0;
                });
    }

    private void waitUntilLoaderDisappears() {
        loader.first().waitFor(isHidden());
    }

    private Locator.WaitForOptions isHidden() {
        return new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN);
    }

    public void backUpFile(String fileTitle, String backupTargetName) {
        Locator fileCopyBackupBtn = getFileCopyBackupButton(fileTitle, backupTargetName);
        page.waitForResponse(fileCopyQueueApi::isEnqueued, fileCopyBackupBtn::click);
    }

    private Locator getFileCopyBackupButton(String fileTitle, String backupTargetName) {
        return getFileCopyItem(fileTitle, backupTargetName)
                .getByTestId(BACKUP_FILE_COPY_BTN_TEST_ID);
    }

    private Locator getFileCopyItem(String fileTitle, String backupTargetName) {
        return gameList
                .getByTestId(SOURCE_FILE_TEST_ID)
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
