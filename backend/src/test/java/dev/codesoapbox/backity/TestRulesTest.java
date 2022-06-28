package dev.codesoapbox.backity;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.fail;

@AnalyzeClasses(packagesOf = BackityApplication.class)
public class TestRulesTest {

    @ArchTest
    public static void testClassesShouldResideInCorrectPackage(JavaClasses classes) {
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

    private static List<String> findTestPackageViolations(JavaClasses classes, Map<String, String> testNamesByPackage) {
        List<String> errors = new ArrayList<>();

        for (JavaClass classUnderTest : classes) {
            String testName = classUnderTest.getSimpleName() + "Test";
            String testPackage = testNamesByPackage.get(testName);
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
