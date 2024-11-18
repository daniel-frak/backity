package dev.codesoapbox.backity.e2e.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import lombok.Getter;

public class AuthenticationPage {

    private final Page page;
    private final Locator logInToGogBtn;
    private final Locator gogCodeUrlInput;
    private final Locator gogAuthenticateButton;

    @Getter
    private final Locator gogAuthBadge;

    public AuthenticationPage(Page page) {
        this.page = page;
        logInToGogBtn = page.getByTestId("log-in-to-gog-btn");
        gogCodeUrlInput = page.getByTestId("gog-code-url-input");
        gogAuthenticateButton = page.getByTestId("gog-authenticate-btn");
        gogAuthBadge = page.getByTestId("gog-auth-status");
    }

    public void navigate() {
        page.navigate("/");
    }

    public void authenticateGog() {
        // Not strictly necessary:
        logInToGogBtn.click();
        Page newTab = page.context().waitForPage(logInToGogBtn::click);
        newTab.waitForLoadState();
        newTab.close();
        // END Not strictly necessary

        gogCodeUrlInput.fill("http://localhost/test-login-result?code=test_code");
        gogAuthenticateButton.click();
    }
}
