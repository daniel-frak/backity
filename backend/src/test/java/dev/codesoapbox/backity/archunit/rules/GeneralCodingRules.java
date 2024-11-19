package dev.codesoapbox.backity.archunit.rules;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.GeneralCodingRules.*;

@SuppressWarnings("unused")
public class GeneralCodingRules {

    private static final String CONFIG_PACKAGE =
            ".." + PortsAndAdaptersArchitectureRules.Constants.CONFIG_PACKAGE + "..";
    private static final String SPRING_COMPONENT_ANNOTATION = "org.springframework.stereotype.Component";
    private static final String SPRING_CONFIGURATION_ANNOTATION =
            "org.springframework.context.annotation.Configuration";

    @ArchTest
    static final ArchRule INTERFACE_NAMES_SHOULD_NOT_START_WITH_I =
            noClasses().that().areInterfaces().should().haveNameMatching(".*\\.I[A-Z][A-Za-z0-9_-]*")
                    .because("the prefix does not add any useful information");

    @ArchTest
    static final ArchRule INTERFACE_NAMES_SHOULD_NOT_END_WITH_I =
            noClasses().that().areInterfaces().should().haveNameMatching(".*I")
                    .because("the suffix does not add any useful information");

    @ArchTest
    static final ArchRule INTERFACE_NAMES_SHOULD_NOT_CONTAIN_THE_WORD_INTERFACE =
            noClasses().that().areInterfaces().should().haveSimpleNameContaining("Interface")
                    .because("the word does not add any useful information");

    @ArchTest
    static final ArchRule NO_CLASS_SHOULD_END_WITH_IMPL_UNLESS_GENERATED = noClasses().that()
            .areNotAssignableTo(annotatedWith("org.mapstruct.Mapper"))
            .should().haveSimpleNameEndingWith("Impl")
            .because("Only generated classes (e.g. with MapStruct) can end with Impl, as the suffix does not" +
                     " contain any useful information");

    @ArchTest
    static final ArchRule GENERIC_EXCEPTIONS_SHOULD_NOT_BE_THROWN = NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;

    @ArchTest
    static final ArchRule JAVA_UTIL_LOGGING_SHOULD_NOT_BE_USED = NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;

    @ArchTest
    static final ArchRule JODA_TIME_SHOULD_NOT_BE_USED = NO_CLASSES_SHOULD_USE_JODATIME;

    @ArchTest
    static final ArchRule STANDARD_STREAMS_SHOULD_NOT_BE_USED = NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;

    @ArchTest
    static final ArchRule FIELD_INJECTION_SHOULD_NOT_BE_USED = noFields().that()
            .areDeclaredInClassesThat()
            .areNotAnnotatedWith("org.springframework.context.annotation.Configuration")
            .should(BE_ANNOTATED_WITH_AN_INJECTION_ANNOTATION)
            .as("no classes should use field injection")
            .because("""
                    field injection is considered harmful; use constructor injection or setter injection instead; \
                    see https://stackoverflow.com/q/39890849 for detailed explanations""");

    @ArchTest
    static final ArchRule BEANS_SHOULD_BE_DEFINED_IN_CONFIGURATION = noClasses().that()
            .resideOutsideOfPackage(CONFIG_PACKAGE)
            .should()
            .beAnnotatedWith(SPRING_COMPONENT_ANNOTATION)
            .orShould().beAnnotatedWith("org.springframework.stereotype.Service")
            .because("""
                    1) Domain code should not be polluted with a dependency on Spring, therefore all domain services
                    should be instantiated via @Configuration.
                    
                    2) Services often have additional logic like @Profile, which can make it difficult to tell which
                    beans will be instantiated if this logic is kept on the classes themselves. @Configuration classes,
                    as a bird's eye view of the bean configuration, can provide clarity around what gets instantiated
                    when. Furthermore, using more than one way of bean instantiation can be confusing. Therefore, it is
                    a good practice to instantiate all services using @Configuration.
                    """);

    @ArchTest
    static final ArchRule CONFIGURATION_CLASSES_SHOULD_RESIDE_IN_CORRECT_PACKAGE = classes().that()
            .areAnnotatedWith(SPRING_CONFIGURATION_ANNOTATION)
            .should()
            .resideInAPackage(CONFIG_PACKAGE)
            .because("a consistent structure makes code easier to work with");
}
