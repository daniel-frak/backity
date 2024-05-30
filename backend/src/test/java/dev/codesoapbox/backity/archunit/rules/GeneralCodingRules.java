package dev.codesoapbox.backity.archunit.rules;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.GeneralCodingRules.*;

@SuppressWarnings("unused")
public class GeneralCodingRules {

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
    static final ArchRule FIELD_INJECTION_SHOULD_NOT_BE_USED = NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

    @ArchTest
    static final ArchRule BEANS_SHOULD_BE_DEFINED_IN_CONFIGURATION = noClasses().that()
            .resideOutsideOfPackage("..config..")
            .should()
            .beAnnotatedWith("org.springframework.stereotype.Component")
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
}
