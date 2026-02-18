package dev.codesoapbox.backity.buildtools;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

public record ParentClassInfo(
        String packageName,
        String className
) {

    public String getNameWithoutTestSuffix() {
        return className.replaceAll(testSuffix() + "$", "");
    }

    public String testSuffix() {
        if (className.endsWith("IT")) {
            return "IT";
        } else {
            return "Test";
        }
    }

    public static ParentClassInfo build(Elements elements, TypeElement parentClass) {
        String packageName = elements.getPackageOf(parentClass).getQualifiedName().toString();
        String className = parentClass.getSimpleName().toString();

        return new ParentClassInfo(packageName, className);
    }
}
