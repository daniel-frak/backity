package dev.codesoapbox.backity.archunit.production.rules.architecture.dependencies;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import dev.codesoapbox.backity.BackityApplication;
import dev.codesoapbox.backity.archunit.production.rules.ArchitectureRules;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/// Rules about how modules should depend on each other.
@SuppressWarnings("unused")
public class ModuleIsolationRules {

    @ArchTest
    static final ArchRule CORE_SHOULD_NOT_DEPEND_ON_INTEGRATIONS = noClasses().that()
            .resideInAPackage(BackityApplication.class.getPackageName() + ".core..")
            .should().dependOnClassesThat()
            .resideInAPackage(BackityApplication.class.getPackageName() + ".integrations..")
            .because("""
                    making the core unaware of specific integrations will increase maintainability.
                    
                    Context:
                    Referencing integrations in the core package is convenient.
                    However, it would tightly couple the core to those integrations, violating separation of concerns.
                    This would make the core more difficult to reason about and require modifications to the core \
                    when an integration changes.
                    
                    Positive consequences:
                    - Improves maintainability by keeping core logic independent of external dependencies.
                    - Enhances modularity, allowing integrations to evolve separately without affecting the core.
                    
                    Negative consequences:
                    - Adds some complexity due to requiring an additional layer of abstraction.
                    """);

    @ArchTest
    static final ArchRule SHARED_SHOULD_NOT_DEPEND_ON_OTHER_MODULES = noClasses().that()
            .resideInAPackage(BackityApplication.class.getPackageName() + ".shared..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                    BackityApplication.class.getPackageName() + ".core..",
                    BackityApplication.class.getPackageName() + ".integrations.."
            )
            .because("""
                circular dependencies between shared and other modules would undermine modularity \
                and complicate builds and testing.
                
                Context:
                The shared module is intended to provide common utilities and abstractions \
                that other modules can depend on.
                Referencing other modules from shared would invert this dependency direction, \
                creating circular dependencies and tightly coupling shared to the rest of the codebase.
                This would undermine the purpose of the shared module and make it difficult to reuse \
                or reason about in isolation.
                
                Positive consequences:
                - Preserves the shared module as a stable, reusable foundation for other modules.
                - Prevents circular dependencies between shared and the modules that depend on it.
                - Makes the shared module easier to reason about and test in isolation.
                """);

    @ArchTest
    static final ArchRule GAME_PROVIDER_INTEGRATIONS_SHOULD_NOT_DEPEND_ON_EACH_OTHER = slices()
            .matching(ArchitectureRules.Constants.GAME_PROVIDERS_PACKAGE_PATTERN)
            .should().notDependOnEachOther()
            .ignoreDependency(
                    resideInAnyPackage(ArchitectureRules.Constants.GAME_PROVIDERS_PACKAGE_PATTERN),
                    resideInAPackage(ArchitectureRules.Constants.GAME_PROVIDERS_SHARED_PACKAGE_PATTERN))
            .because("""
                    making integrations unaware of each other will increase maintainability.
                    
                    Context:
                    Integrations may need to interact with each other, \
                    but referencing other integrations creates dependencies between them.
                    This introduces coupling, meaning a change in one integration \
                    could require modifications in another, reducing their ability to evolve independently.
                    By keeping integrations isolated, each can be modified, replaced, or extended \
                    without unintended side effects in other integrations.
                    
                    Positive consequences:
                    - Improves maintainability by preventing unnecessary dependencies between integrations.
                    - Allows integrations to evolve independently without impacting others.
                    - Reduces the risk of cascading changes when modifying or replacing an integration.
                    
                    Negative consequences:
                    - Adds some complexity due to requiring an additional layer of abstraction, if integrations
                    need to communicate with each other.
                    """);
}
