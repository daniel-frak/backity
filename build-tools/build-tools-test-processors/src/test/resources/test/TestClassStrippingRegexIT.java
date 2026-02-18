package test;

import dev.codesoapbox.backity.buildtools.MultiAnnotationTest;
import test.annotations.ConcreteAnnotation;

@MultiAnnotationTest(
        value = ConcreteAnnotation.class,
        stripFromClassNameRegex = "StrippingRegex|Annotation"
)
public abstract class TestClassStrippingRegexIT {
}
