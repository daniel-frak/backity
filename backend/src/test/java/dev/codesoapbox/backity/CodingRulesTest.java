package dev.codesoapbox.backity;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.GeneralCodingRules.*;

@AnalyzeClasses(packagesOf = BackityApplication.class, importOptions = ImportOption.DoNotIncludeTests.class)
public class CodingRulesTest {

    @ArchTest
    static final ArchRule interfaceNamesShouldNotStartWithI =
            noClasses().that().areInterfaces().should().haveNameMatching(".*\\.I[A-Z][A-Za-z0-9_-]*");

    @ArchTest
    static final ArchRule interfaceNamesShouldNotEndWithI =
            noClasses().that().areInterfaces().should().haveNameMatching(".*I");

    @ArchTest
    static final ArchRule interfaceNamesShouldNotContainTheWordInterface =
            noClasses().that().areInterfaces().should().haveSimpleNameContaining("Interface");

    @ArchTest
    static final ArchRule noClassShouldEndWithImplExceptForGenerated = noClasses().that()
            .haveSimpleNameNotEndingWith("MapperImpl")
            .should().haveSimpleNameEndingWith("Impl")
            .because("Only generated classes (e.g. with MapStruct) can end with Impl");
    @ArchTest
    private final ArchRule genericExceptionsShouldNotBeThrown = NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;

    @ArchTest
    private final ArchRule javaUtilLoggingShouldNotBeUsed = NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;

    @ArchTest
    private final ArchRule jodaTimeShouldNotBeUsed = NO_CLASSES_SHOULD_USE_JODATIME;

    @ArchTest
    private final ArchRule standardStreamsShouldNotBeUsed = NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;

    @ArchTest
    private final ArchRule fieldInjectionShouldNotBeUsed = NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

    @ArchTest
    private final ArchRule beansShouldBeDefinedInConfiguration = noClasses().that()
            .resideOutsideOfPackage(ArchitectureTest.CONFIG_PACKAGE)
            .should()
            .beAnnotatedWith(Component.class)
            .orShould().beAnnotatedWith(Service.class);
}
