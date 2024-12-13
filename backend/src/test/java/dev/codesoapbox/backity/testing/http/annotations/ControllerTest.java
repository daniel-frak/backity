package dev.codesoapbox.backity.testing.http.annotations;

import dev.codesoapbox.backity.testing.http.config.SharedControllerTestConfig;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@WebMvcTest
@Import(SharedControllerTestConfig.class)
public @interface ControllerTest {
}
