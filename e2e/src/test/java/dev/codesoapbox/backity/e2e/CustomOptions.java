package dev.codesoapbox.backity.e2e;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.junit.Options;
import com.microsoft.playwright.junit.OptionsFactory;

public class CustomOptions implements OptionsFactory {

    @Override
    public Options getOptions() {
        boolean isHeadless = Boolean.parseBoolean(System.getProperty("headless", "false"));

        return new Options()
                .setHeadless(isHeadless)
                .setContextOptions(new Browser.NewContextOptions()
                        .setBaseURL("http://localhost:8080"));
    }
}
