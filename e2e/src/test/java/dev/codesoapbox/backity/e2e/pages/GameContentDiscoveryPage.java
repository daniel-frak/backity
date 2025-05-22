package dev.codesoapbox.backity.e2e.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import lombok.Getter;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class GameContentDiscoveryPage {

    private final Page page;
    private final Locator startDiscoveryBtn;
    private final Locator discoveryStatusBadge;

    @Getter
    private final Locator discoveredFilesTable;

    public GameContentDiscoveryPage(Page page) {
        this.page = page;
        startDiscoveryBtn = page.getByTestId("start-game-content-discovery-btn");
        discoveryStatusBadge = page.getByTestId("game-content-discovery-status-badge");
        discoveredFilesTable = page.getByTestId("discovered-file-copies-table");
    }

    public void navigate() {
        page.navigate("/game-content-discovery");
    }

    public void discoverAllFiles() {
        startDiscoveryBtn.click();
        waitUntilDiscoveryIsFinished();
    }

    private void waitUntilDiscoveryIsFinished() {
        assertThat(discoveryStatusBadge).containsText("Discovery in progress");
        assertThat(discoveryStatusBadge).not().containsText("Discovery in progress");
    }
}
