package dev.codesoapbox.backity.testing.messaging.application;

import org.awaitility.Awaitility;

import java.time.Duration;

public class ApplicationEventScenario {

    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    public void verifyAfterStartup(Runnable assertion) {
        Awaitility.await()
                .atMost(TIMEOUT)
                .untilAsserted(assertion::run);
    }
}