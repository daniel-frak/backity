package dev.codesoapbox.backity.e2e.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class AuthenticationPage {

    private final Page page;
    private final Locator logInToGogBtn;
    private final Locator gogCodeUrlInput;
    private final Locator gogAuthenticateButton;
    private final Locator gogAuthBadge;
    private final Locator gogLogOutButton;

    public AuthenticationPage(Page page) {
        this.page = page;
        logInToGogBtn = page.getByTestId("log-in-to-gog-btn");
        gogCodeUrlInput = page.getByTestId("gog-code-url-input");
        gogAuthenticateButton = page.getByTestId("gog-authenticate-btn");
        gogAuthBadge = page.getByTestId("gog-auth-status");
        gogLogOutButton = page.getByTestId("log-out-gog-btn");
    }

    public void navigate() {
        page.navigate("/");
    }

    public void authenticateGog() {
        GogLoginPopup gogLoginPopup = summonGogLoginPopup();

        gogLoginPopup.signIn();
        gogCodeUrlInput.fill(gogLoginPopup.getUrl());
        gogLoginPopup.close();

        gogAuthenticateButton.click();
    }

    private GogLoginPopup summonGogLoginPopup() {
        Page loginPopup = page.context().waitForPage(logInToGogBtn::click);
        loginPopup.waitForLoadState();

        return new GogLoginPopup(loginPopup);
    }

    public boolean isAuthenticated() {
        return gogAuthBadge.textContent().contains("Authenticated");
    }

    public void logOut() {
        gogLogOutButton.click();
    }
}
