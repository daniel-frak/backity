package dev.codesoapbox.backity.buildtools;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public final class RecursiveAnnotationFinder {

    private RecursiveAnnotationFinder() {
    }

    public static Optional<AnnotationMirror> find(TypeElement element, String targetAnnotationFqcn) {
        return element.getAnnotationMirrors().stream()
                .flatMap(annotation ->
                        find(targetAnnotationFqcn, annotation, new HashSet<>()).stream())
                .findFirst();
    }

    private static Optional<AnnotationMirror> find(
            String targetAnnotationFqcn, AnnotationMirror annotation, Set<String> visited) {
        TypeElement type = (TypeElement) annotation.getAnnotationType().asElement();
        String annotationFqcn = type.getQualifiedName().toString();

        if (!visited.add(annotationFqcn)) {
            return Optional.empty();
        }

        if (annotationFqcn.equals(targetAnnotationFqcn)) {
            return Optional.of(annotation);
        }

        return type.getAnnotationMirrors().stream()
                .flatMap(innerAnnotation ->
                        find(targetAnnotationFqcn, innerAnnotation, visited).stream())
                .findFirst();
    }
}
