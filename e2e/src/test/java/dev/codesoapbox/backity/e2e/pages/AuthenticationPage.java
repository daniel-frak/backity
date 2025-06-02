package dev.codesoapbox.backity.e2e.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import lombok.Getter;

public class AuthenticationPage {

    private final Page page;
    private final Locator logInToGogBtn;
    private final Locator gogCodeUrlInput;
    private final Locator gogShowAuthModalButton;
    private final Locator gogModalAuthenticateButton;
    @Getter
    private final Locator gogAuthStatus;
    private final Locator gogLogOutButton;

    public AuthenticationPage(Page page) {
        this.page = page;
        logInToGogBtn = page.getByTestId("log-in-to-gog-btn");
        gogCodeUrlInput = page.getByTestId("gog-code-url-input");
        gogShowAuthModalButton = page.getByTestId("show-gog-auth-modal-btn");
        gogModalAuthenticateButton = page.getByTestId("gog-authenticate-btn");
        gogAuthStatus = page.getByTestId("gog-auth-status");
        gogLogOutButton = page.getByTestId("log-out-gog-btn");
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

    public boolean isAuthenticated() {
        return gogAuthStatus.textContent().contains("Authenticated");
    }

    public void logOut() {
        gogLogOutButton.click();
    }
}
