package dev.codesoapbox.backity.buildtools;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

final class NestedClassFinder {

    /// Static classes don't depend on the enclosing instance, so there is nothing to redeclare.
    /// Private classes are inaccessible.
    /// Final classes can't be extended.
    /// Abstract classes would likely need to generate method implementations.
    private static final Set<Modifier> UNSUITABLE_MODIFIERS =
            EnumSet.of(Modifier.STATIC, Modifier.PRIVATE, Modifier.FINAL, Modifier.ABSTRACT);

    private NestedClassFinder() {
    }

    static List<NestedClassInfo> findNestedClasses(TypeElement parentClass) {
        return parentClass.getEnclosedElements().stream()
                .filter(element -> element.getKind() == ElementKind.CLASS)
                .map(TypeElement.class::cast)
                .filter(element -> Collections.disjoint(element.getModifiers(), UNSUITABLE_MODIFIERS))
                .filter(NestedClassFinder::hasAccessibleNoArgConstructor)
                .map(NestedClassInfo::create)
                .toList();
    }

    private static boolean hasAccessibleNoArgConstructor(TypeElement classElement) {
        return ElementFilter.constructorsIn(classElement.getEnclosedElements()).stream()
                .anyMatch(constructor -> constructor.getParameters().isEmpty()
                        && !constructor.getModifiers().contains(Modifier.PRIVATE));
    }
}
