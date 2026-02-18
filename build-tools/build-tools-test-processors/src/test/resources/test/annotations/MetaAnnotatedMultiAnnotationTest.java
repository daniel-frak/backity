package test.annotations;

import dev.codesoapbox.backity.buildtools.MultiAnnotationTest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.CLASS)
@MultiAnnotationTest(ConcreteAnnotation.class)
public @interface MetaAnnotatedMultiAnnotationTest {
}
