package dev.codesoapbox.backity.e2e.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import dev.codesoapbox.backity.e2e.actions.Repeat;

public class GamesPage {

    private static final String GAMES_URL = "/games";
    private static final String FILE_BACKUP_URL = "/file-backup";

    private final Page page;
    private final Locator loader;
    private final Locator refreshGamesButton;
    private final Locator deleteFileBackupButtons;

    public GamesPage(final Page page) {
        this.page = page;
        this.loader = page.getByTestId("loader");
        this.refreshGamesButton = page.getByTestId("refresh-games-btn");
        this.deleteFileBackupButtons = page.getByTestId("delete-file-backup-btn");
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
                .action(() -> deleteFileBackupButtons.first().click())
                .expectingResponse(response -> response.url().contains(FILE_BACKUP_URL)
                                               && response.request().method().equals("DELETE")
                                               && response.status() == 204)
                .until(() -> !loader.first().isVisible() && !deleteFileBackupButtons.first().isVisible());
    }
}
