package dev.codesoapbox.backity.e2e.actions;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.fail;

@RequiredArgsConstructor
public class Repeat {

    private final Page page;

    public static Repeat on(Page page) {
        return new Repeat(page);
    }

    public WithAction action(Runnable action) {
        return new WithAction(action);
    }

    @RequiredArgsConstructor
    public class WithAction {

        private final Runnable action;

        public WithResponseCondition expectingResponse(Function<Response, Boolean> responseCondition) {
            return new WithResponseCondition(responseCondition);
        }

        @RequiredArgsConstructor
        public class WithResponseCondition {

            private final Function<Response, Boolean> responseCondition;

            public void until(BooleanSupplier exitCondition) {
                if (exitCondition.getAsBoolean()) {
                    return;
                }

                var retryCounter = new AtomicInteger(10);

                do {
                    page.waitForResponse(responseCondition::apply, action);
                    if (retryCounter.get() > 0) {
                        page.waitForTimeout(1000);
                    }
                } while (!exitCondition.getAsBoolean() && retryCounter.decrementAndGet() != 0);

                if(!exitCondition.getAsBoolean()) {
                    fail("The exit condition was not met");
                }
            }
        }
    }
}
