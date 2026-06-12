package dev.codesoapbox.backity.archunit.production.rules.architecture;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import dev.codesoapbox.backity.archunit.production.rules.ArchitectureRules;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/// Rules about package structure.
@SuppressWarnings("unused")
public class PackagingRules {

    @ArchTest
    static final ArchRule EXCEPTIONS_SHOULD_BE_IN_CORRECT_PACKAGE = classes().that()
            .areAssignableTo(Exception.class)
            .should().resideInAPackage(ArchitectureRules.Constants.EXCEPTIONS_PACKAGE_PATTERN)
            .because("""
                    a consistent structure makes code easier to work with.
                    
                    Context:
                    If exceptions are scattered across the project, developers \
                    may struggle to find and use them properly.
                    This can lead to duplicated error handling and messy code. \
                    Keeping exceptions in a dedicated package makes it easier to manage and understand them.
                    
                    Positive consequences:
                    - Provides a predictable location for all exception-related classes.
                    - Reduces confusion by maintaining a consistent project structure.
                    
                    Neutral consequences:
                    - Exception definitions will still be distributed across modules, \
                    meaning some fragmentation remains.
                    
                    Negative consequences:
                    - May introduce additional complexity in small modules, \
                    as each feature package may need its own exception subpackage.
                    """);

    @ArchTest
    static final ArchRule REPOSITORY_IMPLEMENTATIONS_SHOULD_RESIDE_IN_CORRECT_PACKAGE = classes().that()
            .areNotInterfaces().and()
            .haveNameMatching(".*Repository")
            .should().resideInAPackage(ArchitectureRules.Constants.PERSISTENCE_ADAPTER_PACKAGE_PATTERN)
            .because("""
                    a consistent structure makes code easier to work with.
                    
                    Context:
                    If repository implementations are scattered across the project, developers \
                    may struggle to find and use them properly. This can lead to duplicated and messy code.
                    Keeping them in a dedicated package makes it easier to manage and understand them.
                    
                    Positive consequences:
                    - Makes repository implementations easier to locate and manage.
                    - Reduces confusion by maintaining a consistent project structure.
                    
                    Neutral consequences:
                    - Repository definitions will still be spread across feature packages.
                    
                    Negative consequences:
                    - Can introduce unnecessary subpackage complexity in simple modules.
                    """);

    @ArchTest
    static final ArchRule CONTROLLERS_SHOULD_RESIDE_IN_CORRECT_PACKAGE = classes().that()
            .areMetaAnnotatedWith(RestController.class)
            .should().resideInAPackage(ArchitectureRules.Constants.CONTROLLER_PACKAGE_PATTERN)
            .because("""
                    a consistent structure makes code easier to work with.
                    
                    Context:
                    If controllers are scattered across the project, developers \
                    may struggle to find and use them properly. This can lead to duplicated and messy code.
                    Keeping them in a dedicated package makes it easier to manage and understand them.
                    
                    Positive consequences:
                    - Makes controllers easier to locate and manage.
                    - Reduces confusion by maintaining a consistent project structure.
                    
                    Neutral consequences:
                    - Controllers will still be spread across feature packages.
                    
                    Negative consequences:
                    - Can introduce unnecessary subpackage complexity in simple modules.
                    """);

    @ArchTest
    static final ArchRule CONFIGURATION_CLASSES_SHOULD_RESIDE_IN_CORRECT_PACKAGE = classes().that()
            .areMetaAnnotatedWith(Configuration.class)
            .and().areNotAnnotatedWith(SpringBootApplication.class)
            .should()
            .resideInAPackage(ArchitectureRules.Constants.CONFIG_PACKAGE_PATTERN)
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
