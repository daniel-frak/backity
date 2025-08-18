package dev.codesoapbox.backity.testing.time.config;

import dev.codesoapbox.backity.testing.time.FakeClock;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

public class ResetClockTestExecutionListener implements TestExecutionListener {

    @Override
    public void afterTestMethod(TestContext testContext) {
        FakeClock fakeClock = getFakeClock(testContext);
        fakeClock.reset();
    }

    private FakeClock getFakeClock(TestContext testContext) {
        ApplicationContext applicationContext = testContext.getApplicationContext();
        return applicationContext.getBean(FakeClock.class);
    }
}
