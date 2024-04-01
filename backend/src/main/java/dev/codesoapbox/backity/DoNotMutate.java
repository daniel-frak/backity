package dev.codesoapbox.backity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Stops Pitest from mutating a method
 */
@Retention(value = RetentionPolicy.CLASS)
public @interface DoNotMutate {
}
