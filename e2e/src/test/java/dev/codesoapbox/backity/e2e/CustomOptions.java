package dev.codesoapbox.backity.e2e;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.junit.Options;
import com.microsoft.playwright.junit.OptionsFactory;

import java.nio.file.Paths;

public class CustomOptions implements OptionsFactory {

    public static final String BASE_URL = "http://localhost:8080";

    @Override
    public Options getOptions() {
        boolean isHeadless = Boolean.parseBoolean(System.getProperty("headless", "false"));

        return new Options()
                .setHeadless(isHeadless)
                .setContextOptions(new Browser.NewContextOptions()
                        .setBaseURL(BASE_URL))
                .setTrace(Options.Trace.RETAIN_ON_FAILURE)
                .setOutputDir(Paths.get("target/playwright-traces"));
    }
}
