package dev.codesoapbox.backity.shared.adapters.driving.api.http.lowercaseenums.openapi;

import java.lang.annotation.*;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation which indicates that an enum's values should be lowercase in the API
 */
@Target({ElementType.TYPE})
@Retention(RUNTIME)
@Documented
@Inherited
public @interface LowercaseApiEnum {
}
