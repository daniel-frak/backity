package test;

import dev.codesoapbox.backity.buildtools.MultiAnnotationTest;
import test.annotations.ConcreteAnnotation;

@MultiAnnotationTest(ConcreteAnnotation.class)
public abstract class TestClassWithExistingChildIT {
}
