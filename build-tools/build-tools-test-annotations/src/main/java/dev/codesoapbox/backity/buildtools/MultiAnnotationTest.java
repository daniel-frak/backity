package dev.codesoapbox.backity.buildtools;

import java.lang.annotation.*;

/**
 * Declares that the annotated test class should be materialized into multiple concrete subclasses,
 * each annotated with one of the provided annotation types.
 *
 * <p>This annotation is processed at compile time by {@code MultiAnnotationTestProcessor}.
 * For every annotation type declared in {@link #value()}, a new subclass is generated that:
 *
 * <ul>
 *     <li>Extends the annotated class</li>
 *     <li>Is annotated with the respective concrete annotation</li>
 * </ul>
 *
 * <p>This enables executing the same test suite multiple times with different
 * class-level configurations (e.g., different database setups, environments, or Spring contexts)
 * without manually creating subclasses.
 *
 * <h2>Meta-annotation support</h2>
 * This annotation may also be used as a meta-annotation. In that case,
 * classes annotated with a custom annotation that itself carries
 * {@code @MultiAnnotationTest} will be processed in the same way.
 *
 * <h2>Generated class naming</h2>
 * The name of each generated subclass is derived from the original class name, with the concrete annotation name
 * appended at the end.
 * <p>If {@link #stripFromClassNameRegex()} is specified, the provided regular
 * expression is used to remove parts of the generated class name.
 *
 * <p><strong>Note:</strong> If a subclass with the same fully qualified name
 * already exists, generation will be skipped.
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface MultiAnnotationTest {

    /**
     * Annotation types that will be applied to generated subclasses.
     */
    Class<? extends Annotation>[] value();

    /**
     * A regular expression for what should be removed from the generated subclass names.
     */
    String stripFromClassNameRegex() default "";
}
