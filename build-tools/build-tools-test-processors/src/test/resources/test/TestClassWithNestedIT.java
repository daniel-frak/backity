package test;

import dev.codesoapbox.backity.buildtools.MultiAnnotationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import test.annotations.ConcreteAnnotation;

@MultiAnnotationTest(ConcreteAnnotation.class)
public abstract class TestClassWithNestedIT {

    @Nested
    class NestedWithOneAnnotation {
    }

    @Nested
    @DisplayName("Custom name")
    class NestedWithMultipleAnnotations {
    }

    class NestedWithNoAnnotations {
    }

    // Should be ignored: static
    static class NestedStatic {
    }

    // Should be ignored: private
    private class NestedPrivate {
    }

    // Should be ignored: final
    final class NestedFinal {
    }

    // Should be ignored: abstract
    abstract class NestedAbstract {
    }

    // Should be ignored: no accessible no-arg constructor
    class NestedWithPrivateNoArgsConstructor {

        private NestedWithPrivateNoArgsConstructor() {
        }
    }

    // Should be ignored: no accessible constructor without parameters
    class NestedWithoutNoArgsConstructor {

        NestedWithoutNoArgsConstructor(String ignored) {
        }
    }
}
