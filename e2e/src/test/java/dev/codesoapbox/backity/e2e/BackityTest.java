package dev.codesoapbox.backity.e2e;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import dev.codesoapbox.backity.e2e.pages.AuthenticationPage;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@UsePlaywright(CustomOptions.class)
class BackityTest {

    @Test
    void shouldLogIntoGog(Page page) {
        AuthenticationPage authenticationPage = new AuthenticationPage(page);
        authenticationPage.navigate();

        authenticationPage.authenticateGog();

        assertThat(authenticationPage.getGogAuthBadge()).containsText("Authenticated");
    }
}
