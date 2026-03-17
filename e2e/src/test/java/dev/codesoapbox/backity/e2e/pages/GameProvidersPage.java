package dev.codesoapbox.backity.e2e.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.LocatorAssertions;
import lombok.Getter;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class GameProvidersPage {

    public static final String NOT_AUTHENTICATED_LOWERCASE = "not authenticated";

    private final Page page;
    private final Locator logInToGogBtn;
    private final Locator gogCodeUrlInput;
    private final Locator gogShowAuthModalButton;
    private final Locator gogModalAuthenticateButton;
    private final Locator gogAuthStatus;
    private final Locator gogLogOutButton;
    private final Locator startDiscoveryBtn;
    private final Locator lastDiscoveryTimestamp;

    @Getter
    private final Locator discoveredFilesTable;

    public GameProvidersPage(Page page) {
        this.page = page;
        logInToGogBtn = page.getByTestId("log-in-to-gog-btn");
        gogCodeUrlInput = page.getByTestId("gog-code-url-input");
        gogShowAuthModalButton = page.getByTestId("show-gog-auth-modal-btn");
        gogModalAuthenticateButton = page.getByTestId("gog-authenticate-btn");
        gogAuthStatus = page.getByTestId("gog-auth-status");
        gogLogOutButton = page.getByTestId("log-out-gog-btn");
        startDiscoveryBtn = page.getByTestId("start-game-content-discovery-btn");
        lastDiscoveryTimestamp = page.getByTestId("last-discovery-timestamp");
        discoveredFilesTable = page.getByTestId("discovered-file-copies-table");
    }

    public void navigate() {
        page.navigate("/");
    }

    public void authenticateGog() {
        gogShowAuthModalButton.click();
        GogLoginPopup gogLoginPopup = summonGogLoginForm();

        gogLoginPopup.signIn();
        gogCodeUrlInput.fill(gogLoginPopup.getUrl());
        gogLoginPopup.close();

        gogModalAuthenticateButton.click();
    }

    private GogLoginPopup summonGogLoginForm() {
        Page loginPopup = page.context().waitForPage(logInToGogBtn::click);
        loginPopup.waitForLoadState();

        return new GogLoginPopup(loginPopup);
    }

    public Locator getAuthenticationStatusLocator() {
        return gogAuthStatus;
    }

    public boolean isGogAuthenticated() {
        return !gogAuthStatus.textContent().toLowerCase().contains(NOT_AUTHENTICATED_LOWERCASE);
    }

    public void logOutFromGog() {
        gogLogOutButton.click();
    }

    public void discoverAllFiles() {
        String previousTimestamp = lastDiscoveryTimestamp.textContent();

        startDiscoveryBtn.click();

        waitUntilDiscoveryIsFinished(previousTimestamp);
    }

    private void waitUntilDiscoveryIsFinished(String previousTimestamp) {
        assertThat(lastDiscoveryTimestamp)
                .not()
                .containsText(previousTimestamp, new LocatorAssertions.ContainsTextOptions());
        assertThat(startDiscoveryBtn).isEnabled();
    }
}
