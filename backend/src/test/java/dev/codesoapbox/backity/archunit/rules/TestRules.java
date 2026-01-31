package dev.codesoapbox.backity.archunit.rules;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.assertj.core.api.Fail.fail;

@SuppressWarnings("unused")
public class TestRules {

    @ArchTest
    static final ArchRule ASSERTJ_SHOULD_BE_USED_INSTEAD_OF_JUNIT_ASSERTIONS =
            noClasses().that().resideOutsideOfPackage("..archunit..").should()
                    .dependOnClassesThat()
                    .haveFullyQualifiedName(org.junit.jupiter.api.Assertions.class.getName())
                    .because("AssertJ assertions provide a more fluent and expressive API," +
                            " offering a wider range of assertion options and clearer failure messages");

    /**
     * Ensures that all test classes with the same name as the class they're testing reside in the same package as that
     * class. E.g. SomeClassTest should reside in the same package as SomeClass.
     */
    @ArchTest
    static void testClassesShouldResideInCorrectPackage(JavaClasses classes) {
        Map<String, String> testNamesByPackage = getTestNamesByPackage(classes);
        List<String> errors = findTestPackageViolations(classes, testNamesByPackage);

        if (!errors.isEmpty()) {
            fail(String.join("\n", errors));
        }
    }

    private static Map<String, String> getTestNamesByPackage(JavaClasses classes) {
        Map<String, String> testNamesByPackage = new HashMap<>();
        for (JavaClass clazz : classes) {
            if (clazz.getName().endsWith("Test") || clazz.getName().endsWith("IT")) {
                testNamesByPackage.put(clazz.getSimpleName(), clazz.getPackage().getName());
            }
        }
        return testNamesByPackage;
    }

    private static List<String> findTestPackageViolations(JavaClasses classes, Map<String, String> testPackagesByName) {
        List<String> errors = new ArrayList<>();

        for (JavaClass classUnderTest : classes) {
            String testName = classUnderTest.getSimpleName() + "Test";
            String testPackage = testPackagesByName.get(testName);
            boolean testExistsButIsInWrongPackage = testPackage != null
                    && !testPackage.equals(classUnderTest.getPackage().getName());

            if (testExistsButIsInWrongPackage) {
                errors.add(String.format("Test class %s.%s is in the wrong package - should be in %s.",
                        testPackage, testName, classUnderTest.getPackage().getName()));
            }
        }

        return errors;
    }
}
