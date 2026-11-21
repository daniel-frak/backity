package dev.codesoapbox.backity.buildtools;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import java.util.List;

public record NestedClassInfo(
        String name,
        List<String> annotations
) {

    public static NestedClassInfo create(TypeElement nestedClass) {
        List<String> annotations = nestedClass.getAnnotationMirrors().stream()
                .map(AnnotationMirror::toString)
                .toList();

        return new NestedClassInfo(
                nestedClass.getSimpleName().toString(),
                annotations
        );
    }
}
