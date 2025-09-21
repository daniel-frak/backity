package dev.codesoapbox.backity.testing.messaging.annotations;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.*;

/**
 * Annotation for a domain event listener.
 *
 * <h1>Motivation for shared context</h1>
 * <p>
 * While creating an application context for event handler tests does not take a long time, it can lead to
 * the context cache filling up and evicting other, more expensive contexts (such as those for testing repositories).
 * <p>
 * Thus, making all domain event handler tests share a single application context should protect against
 * cache eviction slowing down the tests.
 */
@ExtendWith(SpringExtension.class)
@SpringEventListenerTestBeans
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface SpringEventListenerTest {
}
