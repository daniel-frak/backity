package dev.codesoapbox.backity.testing.http.annotations;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.lang.annotation.*;

/**
 * Annotation for a controller test.
 * <p>
 * Mocks every use case and injects a fake clock bean.
 *
 * <h1>Motivation for shared context</h1>
 * <p>
 * While creating an application context for controller tests does not take a long time, it can lead to the context
 * cache filling up and evicting other, more expensive contexts (such as those for testing repositories).
 * <p>
 * Thus, making all controller tests share a single application context should protect against cache eviction slowing
 * down the tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@WebMvcTest
@ConfigureSharedControllerTestBeans
public @interface ControllerTest {
}
