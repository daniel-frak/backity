package dev.codesoapbox.backity.e2e.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import dev.codesoapbox.backity.e2e.actions.Repeat;
import lombok.Getter;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class GameContentDiscoveryPage {

    private static final String GAME_FILES_DISCOVERED_URL = "/game-files?processing-status=discovered";
    private static final String GAME_FILES_ENQUEUE_URL = "/game-file-actions/enqueue";

    private final Page page;
    private final Locator startDiscoveryBtn;
    private final Locator discoveryStatusBadge;
    private final Locator refreshDiscoveredFilesBtn;

    @Getter
    private final Locator discoveredFilesTable;

    public GameContentDiscoveryPage(Page page) {
        this.page = page;
        startDiscoveryBtn = page.getByTestId("start-game-content-discovery-btn");
        discoveryStatusBadge = page.getByTestId("game-content-discovery-status-badge");
        refreshDiscoveredFilesBtn = page.getByTestId("refresh-discovered-files-btn");
        discoveredFilesTable = page.getByTestId("discovered-files-table");
    }

    public void navigate() {
        page.navigate("/game-content-discovery");
    }

    public void startDiscovery() {
        startDiscoveryBtn.click();
        waitUntilDiscoveryIsFinished();
        refreshDiscoveredFilesBtn.click();
    }

    private void waitUntilDiscoveryIsFinished() {
        assertThat(discoveryStatusBadge).not().containsText("Discovery in progress");
    }

    public Locator getDiscoveredFileRow(String gameProviderIdGameTitle) {
        refreshDiscoveredFilesUntilTheyContain(gameProviderIdGameTitle);
        return discoveredFilesTable.locator("tr")
                .filter(new Locator.FilterOptions().setHasText(gameProviderIdGameTitle));
    }

    private void refreshDiscoveredFilesUntilTheyContain(String fileTitle) {
        Repeat.on(page)
                .action(refreshDiscoveredFilesBtn::click)
                .expectingResponse(response ->
                        response.url().contains(GAME_FILES_DISCOVERED_URL) && response.status() == 200)
                .until(() -> discoveredFilesTable.textContent().contains(fileTitle));
    }

    public void backUpFile(String fileTitle) {
        refreshDiscoveredFilesUntilTheyContain(fileTitle);
        Locator gameTitleRow = discoveredFilesTable.locator("tr")
                .filter(new Locator.FilterOptions().setHasText(fileTitle));
        Locator backupFileBtn = gameTitleRow.getByTestId("back-up-btn");
        page.waitForResponse(response -> response.url().contains(GAME_FILES_ENQUEUE_URL)
                                         && response.status() == 200,
                backupFileBtn::click);
    }
}
