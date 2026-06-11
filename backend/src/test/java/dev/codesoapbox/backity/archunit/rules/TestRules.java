package dev.codesoapbox.backity.archunit.rules;

import com.tngtech.archunit.base.DescribedPredicate;
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

@SuppressWarnings("unused")
public class TestRules {

    static final List<String> TEST_SUFFIXES = List.of("Test", "IT");
    static final String SHOULD_GIVEN_PATTERN = "(?i)(?=.*should)(?!.*given.*should).*";
    static final String WHEN_OR_IF_PATTERN = ".*(?:^|[_\\W]|)[sS]hould(?:[_\\W]|[A-Z]).*?(?:[Ww]hen|[Ii]f)(?![a-z]).*";
    static final Set<String> STUBBING_ENTRY_POINTS = Set.of(
            "when",            // Mockito.when, LenientStubber.when
            "given",           // BDDMockito.given
            "doReturn", "doThrow", "doAnswer", "doNothing", "doCallRealMethod",
            "lenient",         // Mockito.lenient().when(...)
            "will", "willReturn", "willThrow", "willAnswer", "willDoNothing", "willCallRealMethod" // BDD style
    );

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

    @ArchTest
    static final ArchRule STUBBING_MUST_BE_EXTRACTED_TO_METHODS =
            methods()
                    .that(DescribedPredicate.describe("are test methods", method ->
                            method.isAnnotatedWith(Test.class)
                                    || method.isAnnotatedWith(ParameterizedTest.class)
                                    || method.isAnnotatedWith(RepeatedTest.class)
                                    || method.isAnnotatedWith(BeforeEach.class)
                                    || method.isAnnotatedWith(BeforeAll.class)
                                    || method.isAnnotatedWith(AfterEach.class)
                                    || method.isAnnotatedWith(AfterAll.class)
                    ))
                    .should(new ArchCondition<>("have all mock stubbings extracted to methods") {
                        @Override
                        public void check(JavaMethod method, ConditionEvents events) {
                            method.getMethodCallsFromSelf().stream()
                                    .filter(call ->
                                            call.getTargetOwner().getPackageName().startsWith("org.mockito"))
                                    .filter(call -> STUBBING_ENTRY_POINTS.contains(call.getName()))
                                    .forEach(call -> events.add(SimpleConditionEvent.violated(method,
                                            "Method %s stubs directly via %s in %s".formatted(
                                                    method.getFullName(), call.getName(), call.getSourceCodeLocation())
                                    )));
                        }
                    })
                    .because("""
                            creating a higher-level DSL makes tests clearer and refactoring easier.
                            
                            Context:
                            Tests often need to stub mock logic. However, when reading a test, it's not always clear \
                            what the purpose of a given stubbing is. When mocking is complex or ubiquitous, \
                            a test may become noisy and difficult to understand.
                            
                            Extracting stubbing to methods helps reduce the size of the test method and abstract away
                            details which could otherwise overwhelm the reader. \
                            Naming the method forces the author to articulate why the stub exists, \
                            surfacing hidden assumptions.
                            
                            Arguably, some cases are simple enough that extracting stubbing methods may feel \
                            excessive, such as when a REST controller interacts with a use case. \
                            However, on the whole, this practice has a positive effect \
                            on test readability and maintainability. \
                            It would also be difficult to decide which exceptions to carve out without the rule \
                            becoming hard to reason about. Therefore, the rule is enforced strictly.
                            
                            Positive consequences:
                            - Complex tests will be easier to read and understand due to shorter method bodies \
                            and descriptive helper method names.
                            - Fewer places will need updating when a collaborator's API changes \
                            (or if the mock is replaced with a different kind of test double), due to multiple tests \
                            being able to use the same stubbing method.
                            
                            Neutral consequences:
                            - Readers must navigate one extra method call to see the raw Mockito invocation.
                            - Badly written method names may be more difficult to understand than a direct stubbing \
                            would have been.
                            
                            Negative consequences:
                            - For simple stubbings such as use case execution, extracting stubbing methods may feel \
                            excessive and unnecessary.
                            """);

    private static final Map<String, List<JavaClass>> PRODUCTION_CLASSES_BY_SIMPLE_NAME;

    @ArchTest
    static final ArchRule TEST_CLASSES_SHOULD_RESIDE_IN_CORRECT_PACKAGE = classes()
            .that(new DescribedPredicate<>("are top-level classes") {
                @Override
                public boolean test(JavaClass c) {
                    return c.getEnclosingClass().isEmpty();
                }
            })
            .and(new DescribedPredicate<>("are test classes") {
                @Override
                public boolean test(JavaClass c) {
                    // Is a test class if a recognized suffix was successfully stripped
                    return !stripTestSuffix(c.getSimpleName()).equals(c.getSimpleName());
                }
            })
            .and(new DescribedPredicate<>("have a matching production class") {
                @Override
                public boolean test(JavaClass c) {
                    return PRODUCTION_CLASSES_BY_SIMPLE_NAME.containsKey(stripTestSuffix(c.getSimpleName()));
                }
            })
            .should(new ArchCondition<>("reside in the same package as their production class") {
                @Override
                public void check(JavaClass testClass, ConditionEvents events) {
                    String testedSimpleName = stripTestSuffix(testClass.getSimpleName());
                    List<JavaClass> candidates = PRODUCTION_CLASSES_BY_SIMPLE_NAME.get(testedSimpleName);

                    boolean packageMatches = candidates.stream()
                            .anyMatch(pc -> pc.getPackageName().equals(testClass.getPackageName()));

                    if (!packageMatches) {
                        String expectedPackages = candidates.stream()
                                .map(JavaClass::getPackageName)
                                .collect(Collectors.joining("] or ["));
                        String message = String.format(
                                "%s should be in package [%s] but was found in [%s] %s",
                                testClass.getSimpleName(),
                                expectedPackages,
                                testClass.getPackageName(),
                                testClass.getSourceCodeLocation()
                        );
                        events.add(SimpleConditionEvent.violated(testClass, message));
                    }
                }
            })
            .because("""
                    a consistent structure makes code easier to work with.
                    
                    Context:
                    Test classes are generally created in the same package as what they're testing. \
                    However, subsequent refactorings may cause them to drift, as it's easy to forget to move the tests \
                    when moving the production class. This causes the project structure to become messy, making \
                    tests harder to find than they should be.
                    
                    Positive consequences:
                    - Test classes will be easier to find without the help of the IDE.
                    """);

    static {
        JavaClasses productionClasses = new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackagesOf(BackityApplication.class);

        Map<String, List<JavaClass>> index = new HashMap<>();
        for (JavaClass clazz : productionClasses) {
            if (clazz.getEnclosingClass().isPresent()) continue;
            index.computeIfAbsent(clazz.getSimpleName(), k -> new ArrayList<>()).add(clazz);
        }
        PRODUCTION_CLASSES_BY_SIMPLE_NAME = Collections.unmodifiableMap(index);
    }

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

    private static String stripTestSuffix(String simpleName) {
        for (String suffix : TEST_SUFFIXES) {
            if (simpleName.endsWith(suffix)) {
                return simpleName.substring(0, simpleName.length() - suffix.length());
            }
        }
        return simpleName;
    }
}