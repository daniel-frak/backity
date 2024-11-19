package dev.codesoapbox.backity.e2e.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class GogLoginPopup {

    private final Page page;
    private final Locator logInBtn;

    public GogLoginPopup(Page page) {
        this.page = page;
        this.logInBtn = page.locator("#login_login");
    }

    public void signIn() {
        logInBtn.click();
        page.waitForURL(Pattern.compile(".*on_login_success.*"));
        assertThat(page).hasURL(Pattern.compile(".*code=.*"));
    }

    public String getUrl() {
        return page.url();
    }

    public void close() {
        page.close();
    }
}
