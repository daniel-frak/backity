package dev.codesoapbox.backity.e2e;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.junit.Options;
import com.microsoft.playwright.junit.OptionsFactory;

public class CustomOptions implements OptionsFactory {

    @Override
    public Options getOptions() {
        return new Options()
                .setHeadless(false)
                .setContextOptions(new Browser.NewContextOptions()
                        .setBaseURL("http://localhost:8080"));
    }
}
