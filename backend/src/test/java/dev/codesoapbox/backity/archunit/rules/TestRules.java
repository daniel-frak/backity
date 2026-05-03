package dev.codesoapbox.backity.archunit.rules;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import dev.codesoapbox.backity.BackityApplication;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.*;
import java.util.stream.Collectors;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static org.assertj.core.api.Fail.fail;

@SuppressWarnings("unused")
public class TestRules {

    static final List<String> TEST_SUFFIXES = List.of("Test", "IT");
    static final String SHOULD_GIVEN_PATTERN = "(?i)(?=.*should)(?!.*given.*should).*";
    static final String WHEN_OR_IF_PATTERN = ".*(?:^|[_\\W]|)[sS]hould(?:[_\\W]|[A-Z]).*?(?:[Ww]hen|[Ii]f)(?![a-z]).*";

    @ArchTest
    static final ArchRule ASSERTJ_SHOULD_BE_USED_INSTEAD_OF_JUNIT_ASSERTIONS =
            noClasses().that().resideOutsideOfPackage("..archunit..").should()
                    .dependOnClassesThat()
                    .haveFullyQualifiedName(org.junit.jupiter.api.Assertions.class.getName())
                    .because("AssertJ assertions provide a more fluent and expressive API," +
                            " offering a wider range of assertion options and clearer failure messages");

    @ArchTest
    static final ArchRule DISABLED_TESTS_SHOULD_HAVE_A_REASON =
            methods().that().areAnnotatedWith(Disabled.class)
                    .should(haveNonBlankDisabledReason())
                    .allowEmptyShould(true)
                    .because("""
                            disabled tests without a reason make it unclear why they were disabled \
                            and whether they should be re-enabled or removed""");

    @ArchTest
    static final ArchRule DISABLED_TEST_CLASSES_SHOULD_HAVE_A_REASON =
            classes().that().areAnnotatedWith(Disabled.class)
                    .should(haveNonBlankDisabledReasonOnClass())
                    .allowEmptyShould(true)
                    .because("""
                            disabled tests without a reason make it unclear why they were disabled \
                            and whether they should be re-enabled or removed""");

    @ArchTest
    static final ArchRule TEST_NAMES_SHOULD_FOLLOW_PATTERN =
            methods()
                    .that().areAnnotatedWith(Test.class)
                    .or().areAnnotatedWith(ParameterizedTest.class)
                    .or().areAnnotatedWith(RepeatedTest.class)
                    // No easy way to check this for TestFactory/TestTemplate
                    .should(followShouldGivenConvention())
                    .because("""
                            consistency across the test suite improves readability and reduces
                            cognitive overhead when navigating tests, while enforcing a
                            'should ... (given) ...' structure clarifies intent by stating expected
                            behavior before test context.
                            """);

    @ArchTest
    static final ArchRule TEST_METHOD_NAMES_SHOULD_USE_GIVEN_INSTEAD_OF_WHEN_OR_IF =
            methods()
                    .that().areAnnotatedWith(Test.class)
                    .or().areAnnotatedWith(ParameterizedTest.class)
                    .or().areAnnotatedWith(RepeatedTest.class)
                    .or().areAnnotatedWith(TestFactory.class)
                    .or().areAnnotatedWith(TestTemplate.class)
                    .should(notContainWhenOrIf())
                    .because("""
                            consistency across the test suite reduces cognitive overhead when \
                            navigating tests and helps quickly identify intended behavior. \
                            Use 'given' to describe context instead of 'when' or 'if'.
                            """);

    private static ArchCondition<JavaMethod> followShouldGivenConvention() {
        return new ArchCondition<>("follow 'should...(given)' naming convention") {

            @Override
            public void check(JavaMethod method, ConditionEvents events) {
                Optional<String> displayName = resolveTestDisplayName(method);
                boolean passes = method.getName().matches(SHOULD_GIVEN_PATTERN)
                        || displayName.map(n -> n.matches(SHOULD_GIVEN_PATTERN)).orElse(false);

                if (!passes) {
                    events.add(SimpleConditionEvent.violated(method, String.format(
                            "Method '%s'%s fails the 'should...given' pattern in %s",
                            method.getOwner().getFullName() + "#" + method.getName(),
                            displayName.map(n -> " (display name: '" + n + "')").orElse(""),
                            method.getSourceCodeLocation()
                    )));
                }
            }
        };
    }

    private static ArchCondition<JavaMethod> notContainWhenOrIf() {
        return new ArchCondition<>("not contain 'When' or 'If' in name or display name") {

            @Override
            public void check(JavaMethod method, ConditionEvents events) {
                Optional<String> displayName = resolveTestDisplayName(method);
                boolean methodContainsWhen = method.getName().matches(WHEN_OR_IF_PATTERN);
                boolean displayContainsWhen = displayName.map(n -> n.matches(WHEN_OR_IF_PATTERN))
                        .orElse(false);

                if (methodContainsWhen || displayContainsWhen) {
                    events.add(SimpleConditionEvent.violated(method, String.format(
                            "Method '%s'%s should use 'Given' instead of 'When' or 'If' in %s",
                            method.getOwner().getFullName() + "#" + method.getName(),
                            displayName.map(n -> " (display name: '" + n + "')").orElse(""),
                            method.getSourceCodeLocation()
                    )));
                }
            }
        };
    }

    private static ArchCondition<JavaMethod> haveNonBlankDisabledReason() {
        return new ArchCondition<>("have a non-blank reason in @Disabled") {

            @Override
            public void check(JavaMethod method, ConditionEvents events) {
                String reason = method.getAnnotationOfType(Disabled.class).value();
                if (reason.isBlank()) {
                    events.add(SimpleConditionEvent.violated(method, String.format(
                            "Method '%s' is @Disabled without stating a reason in %s",
                            method.getOwner().getFullName() + "#" + method.getName(),
                            method.getSourceCodeLocation()
                    )));
                }
            }
        };
    }

    private static ArchCondition<JavaClass> haveNonBlankDisabledReasonOnClass() {
        return new ArchCondition<>("have a non-blank reason in @Disabled") {

            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                String reason = javaClass.getAnnotationOfType(Disabled.class).value();
                if (reason.isBlank()) {
                    events.add(SimpleConditionEvent.violated(javaClass, String.format(
                            "Class '%s' is @Disabled without stating a reason in %s",
                            javaClass.getFullName(),
                            javaClass.getSourceCodeLocation()
                    )));
                }
            }
        };
    }

    private static Optional<String> resolveTestDisplayName(JavaMethod method) {
        if (method.isAnnotatedWith(DisplayName.class)) {
            return Optional.of(method.getAnnotationOfType(DisplayName.class).value());
        }
        if (method.isAnnotatedWith(ParameterizedTest.class)) {
            String name = method.getAnnotationOfType(ParameterizedTest.class).name();
            if (!name.equals("{default_display_name}") && !name.isBlank()) {
                return Optional.of(name);
            }
        }
        if (method.isAnnotatedWith(RepeatedTest.class)) {
            String name = method.getAnnotationOfType(RepeatedTest.class).name();
            if (!name.equals(RepeatedTest.SHORT_DISPLAY_NAME) &&
                    !name.equals(RepeatedTest.DISPLAY_NAME_PLACEHOLDER)) {
                return Optional.of(name);
            }
        }
        return Optional.empty();
    }

    /// Ensures that all test classes with the same name as the class they're testing reside in the same package as that
    /// class. E.g., SomeClassTest should reside in the same package as SomeClass.
    @ArchTest
    static void testClassesShouldResideInCorrectPackage(JavaClasses testClasses) {
        JavaClasses productionClasses = new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackagesOf(BackityApplication.class);

        Map<String, List<JavaClass>> productionClassesBySimpleName = new HashMap<>();
        for (JavaClass clazz : productionClasses) {
            if (clazz.getEnclosingClass().isPresent()) {
                continue; // Skip nested, inner, local, and anonymous classes
            }
            productionClassesBySimpleName
                    .computeIfAbsent(clazz.getSimpleName(), k -> new ArrayList<>())
                    .add(clazz);
        }

        List<String> errors = new ArrayList<>();
        for (JavaClass testClass : testClasses) {
            if (testClass.getEnclosingClass().isPresent()) {
                continue; // Skip nested, inner, local, and anonymous classes
            }

            String testedSimpleName = stripTestSuffix(testClass.getSimpleName());
            if (testedSimpleName.equals(testClass.getSimpleName())) {
                continue; // Not a test class (no recognized suffix was stripped)
            }

            List<JavaClass> candidates = productionClassesBySimpleName.get(testedSimpleName);
            if (candidates == null) {
                continue;
            }

            boolean packageMatches = candidates.stream()
                    .anyMatch(pc -> pc.getPackageName().equals(testClass.getPackageName()));

            if (!packageMatches) {
                String expectedPackages = candidates.stream()
                        .map(JavaClass::getPackageName)
                        .collect(Collectors.joining("] or ["));
                errors.add(String.format(
                        "%s should be in package [%s] but was found in [%s] %s",
                        testClass.getSimpleName(),
                        expectedPackages,
                        testClass.getPackageName(),
                        testClass.getSourceCodeLocation()
                ));
            }
        }

        if (!errors.isEmpty()) {
            fail(String.join("\n", errors));
        }
    }

    private static String stripTestSuffix(String simpleName) {
        for (String suffix : TEST_SUFFIXES) {
            if (simpleName.endsWith(suffix)) {
                return simpleName.substring(0, simpleName.length() - suffix.length());
            }
        }
        return simpleName;
    }
}