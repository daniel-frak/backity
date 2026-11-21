package dev.codesoapbox.backity.testing.mocking;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.NestedTestConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.NestedTestConfiguration.EnclosingConfiguration.OVERRIDE;

class MockBeansContextCustomizerFactoryTest {

    private static final String PACKAGE = "some.package";
    private static final String OTHER_PACKAGE = "some.other.package";

    private final MockBeansContextCustomizerFactory factory = new MockBeansContextCustomizerFactory();

    private static MockBeansContextCustomizer expectedCustomizer(Class<?> annotationOwner) {
        MockBeansMatching annotation = Objects.requireNonNull(
                annotationOwner.getDeclaredAnnotation(MockBeansMatching.class),
                () -> "Expected @MockBeansMatching on " + annotationOwner.getName()
        );

        return new MockBeansContextCustomizer(annotation.value());
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @MockBeansMatching(@ComponentScan(PACKAGE))
    @interface MetaAnnotation {
    }

    static class NotAnnotated {
    }

    @MockBeansMatching(@ComponentScan(PACKAGE))
    static class Annotated {
    }

    @MockBeansMatching(@ComponentScan(OTHER_PACKAGE))
    static class AnnotatedWithOtherPackage {
    }

    static class SubclassOfAnnotated extends Annotated {
    }

    @MetaAnnotation
    static class MetaAnnotated {
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    static class NotAnnotatedEnclosing {

        class InnerClass {
        }
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    @MockBeansMatching(@ComponentScan(PACKAGE))
    static class AnnotatedEnclosing {

        class InnerClass {
        }

        @NestedTestConfiguration(OVERRIDE)
        class OverridingInnerClass {
        }
    }

    @Nested
    class CreateContextCustomizer {

        @Test
        void shouldReturnNullGivenNoAnnotation() {
            ContextCustomizer result = factory.createContextCustomizer(NotAnnotated.class, List.of());

            assertThat(result).isNull();
        }

        @Test
        void shouldCreateCustomizerGivenAnnotatedClass() {
            ContextCustomizer result = factory.createContextCustomizer(Annotated.class, List.of());

            assertThat(result).isEqualTo(expectedCustomizer(Annotated.class));
        }

        @Test
        void shouldUseComponentScanDeclaredOnAnnotation() {
            ContextCustomizer result = factory.createContextCustomizer(AnnotatedWithOtherPackage.class, List.of());

            assertThat(result).isEqualTo(expectedCustomizer(AnnotatedWithOtherPackage.class));
        }

        @Test
        void shouldCreateCustomizerGivenAnnotatedSuperclass() {
            ContextCustomizer result = factory.createContextCustomizer(SubclassOfAnnotated.class, List.of());

            assertThat(result).isEqualTo(expectedCustomizer(Annotated.class));
        }

        @Test
        void shouldCreateCustomizerGivenMetaAnnotation() {
            ContextCustomizer result = factory.createContextCustomizer(MetaAnnotated.class, List.of());

            assertThat(result).isEqualTo(expectedCustomizer(MetaAnnotation.class));
        }

        @Test
        void shouldCreateCustomizerGivenAnnotatedEnclosingClass() {
            ContextCustomizer result = factory.createContextCustomizer(AnnotatedEnclosing.InnerClass.class, List.of());

            assertThat(result).isEqualTo(expectedCustomizer(AnnotatedEnclosing.class));
        }

        @Test
        void shouldReturnNullGivenNotAnnotatedEnclosingClass() {
            ContextCustomizer result =
                    factory.createContextCustomizer(NotAnnotatedEnclosing.InnerClass.class, List.of());

            assertThat(result).isNull();
        }

        @Test
        void shouldReturnNullGivenInnerClassOverridesEnclosingConfiguration() {
            ContextCustomizer result =
                    factory.createContextCustomizer(AnnotatedEnclosing.OverridingInnerClass.class, List.of());

            assertThat(result).isNull();
        }
    }
}
