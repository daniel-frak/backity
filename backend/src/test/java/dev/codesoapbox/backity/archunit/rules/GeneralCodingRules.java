package dev.codesoapbox.backity.archunit.rules;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.lang.annotation.Annotation;

import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.GeneralCodingRules.*;

@SuppressWarnings("unused")
public class GeneralCodingRules {

    static final String CONFIG_PACKAGE =
            ".." + PortsAndAdaptersArchitectureRules.Constants.CONFIG_PACKAGE + "..";
    static final String SPRING_COMPONENT_ANNOTATION = "org.springframework.stereotype.Component";
    static final String SPRING_CONFIGURATION_ANNOTATION =
            "org.springframework.context.annotation.Configuration";

    @ArchTest
    static final ArchRule INTERFACE_NAMES_SHOULD_NOT_START_WITH_I =
            noClasses().that().areInterfaces().should().haveNameMatching(".*\\.I[A-Z][A-Za-z0-9_-]*")
                    .because("""
                            the prefix does not add any useful information.
                            
                            Context:
                            Historically, some codebases prefix interfaces with 'I' (e.g., `IService`, `IRepository`). \
                            This practice does not add meaningful information in modern development.
                            
                            Positive consequences:
                            - Interface names will feel more natural and domain-driven, \
                            making them easier to understand at a glance.
                            """);

    @ArchTest
    static final ArchRule INTERFACE_NAMES_SHOULD_NOT_END_WITH_I =
            noClasses().that().areInterfaces().should().haveNameMatching(".*I")
                    .because("""
                            the suffix does not add any useful information.
                            
                            Context:
                            Historically, some codebases suffix interfaces with 'I' (e.g., `IService`, `IRepository`). \
                            This practice does not add meaningful information in modern development.
                            
                            Positive consequences:
                            - Interface names will feel more natural and domain-driven, \
                            making them easier to understand at a glance.
                            """);

    @ArchTest
    static final ArchRule INTERFACE_NAMES_SHOULD_NOT_CONTAIN_THE_WORD_INTERFACE =
            noClasses().that().areInterfaces().should().haveSimpleNameContaining("Interface")
                    .because("""
                            the word does not add any useful information.
                            
                            Context:
                            Historically, some codebases add the word 'Interface' to interface names \
                            (e.g., `ServiceInterface`, `RepositoryInterface`). \
                            This practice does not add meaningful information in modern development.
                            
                            Positive consequences:
                            - Interface names will feel more natural and domain-driven, \
                            making them easier to understand at a glance.
                            """);

    @ArchTest
    static final ArchRule NO_CLASS_SHOULD_END_WITH_IMPL_UNLESS_GENERATED = noClasses().that()
            .areNotAssignableTo(annotatedWith("org.mapstruct.Mapper"))
            .should().haveSimpleNameEndingWith("Impl")
            .because("""
                    the suffix does not contain any useful information.
                    
                    Context:
                    Traditionally, some codebases use the 'Impl' suffix to indicate an implementation of an interface \
                    (e.g., `ServiceImpl`, `RepositoryImpl`). This practice likely originates from older conventions \
                    where it was difficult to mock implementations in tests and so every class had an interface \
                    to facilitate mocking.
                    However, in modern development, this practice is not necessary.
                    
                    Positive consequences:
                    - Reduces unnecessary boilerplate in naming, making the codebase cleaner and more intuitive.
                    """);

    @ArchTest
    static final ArchRule GENERIC_EXCEPTIONS_SHOULD_NOT_BE_THROWN = NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS
            .because("""
                    generic exceptions make it difficult to distinguish between different types of failures.
                    
                    Context:
                    Throwing generic exceptions such as `RuntimeException`, `Exception` or `Throwable` \
                    reduces the granularity of error handling.
                    Developers who catch these exceptions have no clear indication of what type of failure occurred.
                    This makes debugging harder, increases the risk of swallowing important errors, and reduces the \
                    ability to handle failures appropriately.
                    
                    Positive consequences:
                    - Improved debugging and troubleshooting, \
                    since developers can more easily understand the type of failure.
                    - More meaningful stack traces.
                    - Allows better error-handling strategies, where each failure type is handled appropriately.
                    - Greater maintainability, preventing vague and hard-to-trace failures in production systems.
                    
                    Negative consequences:
                    - Requires defining and maintaining a set of meaningful exception classes.
                    """);

    @ArchTest
    static final ArchRule JAVA_UTIL_LOGGING_SHOULD_NOT_BE_USED = NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING
            .because("""
                    java.util.logging is outdated and lacks flexibility compared to modern logging frameworks.
                    
                    Context:
                    While `java.util.logging` is built into Java and provides basic logging capabilities, \
                    it has several limitations that make it unsuitable for modern software development. \
                    It suffers from poor configurability and performance inefficiencies, \
                    particularly in large-scale applications. \
                    More advanced logging frameworks such as SLF4J, Logback, or Log4j offer better flexibility, \
                    structured logging capabilities, and richer configuration options.
                    
                    Positive consequences:
                    - Encourages use of more modern logging frameworks, which provide more robust logging.
                    
                    Negative consequences:
                    - Introduces an additional dependency on third-party logging frameworks, requiring maintenance.
                    """);

    @ArchTest
    static final ArchRule JODA_TIME_SHOULD_NOT_BE_USED = NO_CLASSES_SHOULD_USE_JODATIME
            .because("""
                    Joda-Time is outdated and has been superseded by the java.time package introduced in Java 8.
                    
                    Context:
                    Joda-Time was widely used before Java 8 to handle date and time operations more effectively \
                    than the native Java APIs at the time. However, with the introduction of the `java.time` \
                    package (JSR-310) in Java 8, Joda-Time is no longer necessary. The modern `java.time` \
                    package provides better design, improved clarity, and enhanced functionality, eliminating \
                    many of the issues present in Joda-Time.
                    
                    Positive consequences:
                    - Encourages the use of the officially supported `java.time` package, which is part of the JDK.
                    """);

    @ArchTest
    static final ArchRule STANDARD_STREAMS_SHOULD_NOT_BE_USED = NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS
            .because("""
                    using standard streams directly leads to unstructured logging \
                    and reduces flexibility in input/output management.
                    
                    Context:
                    Standard streams (`System.out`, `System.err`, `System.in`) \
                    are often used for debugging or simple logging. \
                    However, relying on them for structured output can lead to poor maintainability, \
                    unfiltered console clutter, and a lack of control over log management. \
                    Modern logging frameworks such as SLF4J, Logback, or Log4j provide superior control, \
                    configurability, and integration options compared to direct usage of `System.out.println` or \
                    `System.err.println`. Similarly, using `System.in` for input handling bypasses robust input \
                    management strategies and makes code harder to test.
                    
                    Positive consequences:
                    - Ensures proper logging practices by using logging frameworks instead of standard output.
                    - Enhances testability, as logging frameworks and input handling can be mocked \
                    or redirected appropriately.
                    """);

    @ArchTest
    static final ArchRule FIELD_INJECTION_SHOULD_NOT_BE_USED = noFields().that()
            .areDeclaredInClassesThat(new DescribedPredicate<>(
                    "don't have configuration annotation on themselves or on parent classes") {

                @Override
                public boolean test(JavaClass javaClass) {
                    while (javaClass != null) {
                        if(isMetaAnnotated(Configuration.class, javaClass)
                                || isMetaAnnotated(ConfigurationProperties.class, javaClass)) {
                            return false;
                        }
                        javaClass = javaClass.getEnclosingClass().orElse(null);
                    }
                    return true;
                }

                private <T extends Annotation> boolean isMetaAnnotated(
                        Class<T> annotationType, JavaClass javaClass) {
                    return ArchUnitMetaAnnotation.tryGet(annotationType, javaClass.getAnnotations()).isPresent();
                }
            })
            .should(BE_ANNOTATED_WITH_AN_INJECTION_ANNOTATION)
            .as("no classes should use field injection")
            .because("""
                    field injection is considered harmful; \
                    constructor injection or setter injection should be used instead.
                    
                    Context:
                    Field injection (`@Autowired`, `@Inject`) directly assigns dependencies to class fields.
                    While convenient, this approach has multiple downsides:
                    - It bypasses constructor-based dependency injection, \
                      making it harder to enforce required dependencies.
                    - It hinders testability by preventing easy instantiation of objects \
                    without Spring or dependency injection frameworks, creating a tight coupling with them.
                    - It makes makes dependencies less visible, reducing clarity in class design.
                    - It makes it impossible to create immutable objects, such as with constructor injection.
                    - It makes it easy to create a class with many dependencies, encouraging bad design practices.
                    
                    Positive consequences:
                    - Encourages constructor injection, which makes dependencies explicit and mandatory.
                    - Improves testability by allowing easier instantiation of objects without requiring a framework.
                    - Enhances clarity by making dependencies visible in constructors rather than hidden in fields.
                    - Makes immutable dependencies possible, reducing potential runtime errors \
                    and unintended reassignments.
                    
                    Negative consequences:
                    - May introduce boilerplate in cases where many dependencies need to be injected.
                    """);

    @ArchTest
    static final ArchRule BEANS_SHOULD_BE_DEFINED_IN_CONFIGURATION_PACKAGE = noClasses().that()
            .resideOutsideOfPackage(CONFIG_PACKAGE)
            .should()
            .beAnnotatedWith(SPRING_COMPONENT_ANNOTATION)
            .orShould().beAnnotatedWith("org.springframework.stereotype.Service")
            .because("""
                    defining beans outside the configuration package leads to inconsistent dependency management \
                    and reduced clarity in bean instantiation.
                    
                    Context:
                    1) Using more than one way of bean instantiation can be confusing.
                    2) It should be easy to locate bean definitions in the codebase.
                    3) Domain code should not be polluted with a dependency on Spring, yet some domain classes must
                    be instantiated as beans.
                    4) It's a good practice to reduce coupling with the framework wherever possible
                    5) Services often need additional logic like @Profile, which can make it difficult to tell which \
                    beans will be instantiated if this logic is kept on the classes themselves.
                    @Configuration classes, as a bird's eye view of the bean configuration, \
                    can provide clarity around what gets instantiated when.
                    
                    Therefore, it is a good practice to instantiate all services using @Configuration classes, which
                    should be located in a configuration subpackage. However, there may be cases where annotating \
                    a Spring-specific configuration bean is necessary, so using @Component is acceptable in these cases.
                    
                    Positive consequences:
                    - Decouples large parts of the codebase from Spring, making it easier to work with and maintain.
                    - Provides a centralized view of bean instantiation, \
                    making runtime conditions (`@Profile`) easier to manage.
                    - Reduces confusion by standardizing bean definition practices across the codebase.
                    
                    Negative consequences:
                    - May introduce many configuration classes, requiring careful organization to avoid fragmentation.
                    - Developers may be used to annotating beans directly, making it difficult to switch to the new \
                    approach.
                    """);

    @ArchTest
    static final ArchRule CONFIGURATION_CLASSES_SHOULD_RESIDE_IN_CORRECT_PACKAGE = classes().that()
            .areAnnotatedWith(SPRING_CONFIGURATION_ANNOTATION)
            .should()
            .resideInAPackage(CONFIG_PACKAGE)
            .because("""
                    a consistent structure makes code easier to work with.
                    
                    Context:
                    In large codebases, inconsistent placement of configuration classes can lead to confusion, \
                    making it harder for developers to locate and manage application settings. \
                    Scattered configuration definitions can also increase technical debt, as developers may \
                    accidentally duplicate configuration logic or struggle to identify dependencies between components.
                    
                    By enforcing a dedicated package name for configuration classes, \
                    the project maintains clear separation between application logic and dependency management.
                    
                    However, a single package for configuration classes would go against the package-by-feature model,
                    which means that each feature package should have its own configuration sub-package.
                    
                    Positive consequences:
                    - Provides a predictable location for all configuration-related classes.
                    - Reduces confusion by maintaining a consistent project structure.
                    
                    Neutral consequences:
                    - There will likely still be as many configuration packages as there are feature modules,
                    which means configuration will still be somewhat scattered across the codebase.
                    
                    Negative consequences:
                    - Introduces some complexity to simple feature packages, by mandating a dedicated subpackage \
                    for configuration classes.
                    """);
}
