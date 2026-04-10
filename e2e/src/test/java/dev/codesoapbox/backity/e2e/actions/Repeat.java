package dev.codesoapbox.backity.e2e.actions;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.Response;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.logging.Logger;

public class Repeat {

    private static final Logger log = Logger.getLogger(Repeat.class.getName());
    private static final int DEFAULT_MAX_RETRIES = 10;
    private static final double DEFAULT_CONDITION_TIMEOUT_MS = 5_000;
    private static final double DEFAULT_RESPONSE_TIMEOUT_MS = 10_000;

    private final Page page;

    private Repeat(Page page) {
        this.page = page;
    }

    public static Repeat on(Page page) {
        return new Repeat(page);
    }

    public WithAction action(Runnable action) {
        return new WithAction(page, action);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class WithAction {

        private final Page page;
        private final Runnable action;

        public WithResponseCondition expectingResponse(Function<Response, Boolean> responseCondition) {
            return new WithResponseCondition(page, action, responseCondition);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class WithResponseCondition {

        private final Page page;
        private final Runnable action;
        private final Function<Response, Boolean> responseCondition;

        public void until(BooleanSupplier exitCondition) {
            if (exitCondition.getAsBoolean()) {
                return;
            }

            for (int attempt = 1; attempt <= DEFAULT_MAX_RETRIES; attempt++) {
                log.info("Waiting for response, attempt " + attempt + " of " + DEFAULT_MAX_RETRIES);

                boolean isLastAttempt = attempt == DEFAULT_MAX_RETRIES;

                try {
                    page.waitForResponse(
                            responseCondition::apply,
                            new Page.WaitForResponseOptions().setTimeout(DEFAULT_RESPONSE_TIMEOUT_MS),
                            action
                    );
                } catch (PlaywrightException e) {
                    // Response may have arrived just before the timeout; verify before retrying or failing
                    if (exitCondition.getAsBoolean()) {
                        return;
                    }
                    if (isLastAttempt) {
                        throw new AssertionError("The expected response was not received or action timed out", e);
                    }
                    log.warning("Attempt " + attempt + " failed with PlaywrightException, retrying.");
                    continue;
                } catch (Exception e) {
                    throw new AssertionError("Response condition threw unexpectedly on attempt " + attempt, e);
                }

                if (exitCondition.getAsBoolean()) {
                    return;
                }

                try {
                    page.waitForCondition(
                            exitCondition,
                            new Page.WaitForConditionOptions().setTimeout(DEFAULT_CONDITION_TIMEOUT_MS)
                    );
                    return;
                } catch (PlaywrightException e) {
                    if (isLastAttempt) {
                        throw new AssertionError(
                                "The exit condition was not met after " + DEFAULT_MAX_RETRIES + " attempts",
                                e
                        );
                    }
                    log.info("Exit condition not met after attempt " + attempt + ", retrying.");
                }
            }
        }
    }
}
