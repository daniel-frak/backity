package dev.codesoapbox.backity.e2e;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.junit.Options;
import com.microsoft.playwright.junit.OptionsFactory;

import java.nio.file.Paths;

public class CustomOptions implements OptionsFactory {

    @Override
    public Options getOptions() {
        boolean isHeadless = Boolean.parseBoolean(System.getProperty("headless", "false"));

        return new Options()
                .setHeadless(isHeadless)
                .setContextOptions(new Browser.NewContextOptions()
                        .setBaseURL("http://localhost:8080"))
                .setTrace(Options.Trace.RETAIN_ON_FAILURE)
                .setOutputDir(Paths.get("traces"));
    }
}
