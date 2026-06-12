package dev.codesoapbox.backity.archunit.production.rules.architecture.dependencies;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import dev.codesoapbox.backity.archunit.production.rules.ArchitectureRules;
import org.springframework.data.repository.Repository;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/// Rules about isolating infrastructural concerns from the application.
@SuppressWarnings("unused")
public class InfrastructureIsolationRules {

    @ArchTest
    static final ArchRule DOMAIN_SHOULD_NOT_DEPEND_ON_SPRING = noClasses().that()
            .resideInAPackage(ArchitectureRules.Constants.DOMAIN_PACKAGE_PATTERN)
            .should().dependOnClassesThat(resideInAPackage(ArchitectureRules.Constants.SPRING_PACKAGE_PATTERN))
            .because("""
                    domain should not be polluted with infrastructural code.
                    
                    Context:
                    Historically, developers have used Spring annotations \
                    such as @Service and @Transactional for convenience. \
                    While these annotations simplify dependency management, \
                    they also introduce implicit framework dependencies, \
                    making the domain logic harder to test, reuse, and adapt to different environments.
                    
                    However, enforcing this rule strictly means developers can no longer use framework annotations
                    on domain classes, making things like bean instantiation require a bit more code.
                    
                    Positive consequences:
                    - Improves testability and maintainability of domain logic.
                    - Keeps business rules independent of infrastructure frameworks.
                    - Allows domain models to be reused in non-Spring environments.
                    
                    Neutral consequences:
                    - Some developers might prefer framework integrations for convenience.
                    
                    Negative consequences:
                    - Developers can no longer use framework annotations on domain classes, which may introduce
                    more boilerplate code.
                    """);

    @ArchTest
    static final ArchRule APPLICATION_SHOULD_NOT_DEPEND_ON_SPRING = noClasses().that()
            .resideInAPackage(ArchitectureRules.Constants.APPLICATION_PACKAGE_PATTERN)
            .should().dependOnClassesThat(resideInAPackage(ArchitectureRules.Constants.SPRING_PACKAGE_PATTERN))
            .because("""
                    application should not be polluted with infrastructural code.
                    
                    Context:
                    Historically, developers have used Spring annotations \
                    such as @Service and @Transactional for convenience. \
                    While these annotations simplify dependency management, \
                    they also introduce implicit framework dependencies, \
                    making the application logic harder to test, reuse, and adapt to different environments.
                    
                    However, enforcing this rule strictly means developers can no longer use framework annotations
                    on application classes, making things like bean instantiation require a bit more code.
                    
                    Positive consequences:
                    - Improves testability and maintainability of application logic.
                    - Keeps application logic independent of infrastructure frameworks.
                    - Allows application logic to be reused in non-Spring environments.
                    
                    Neutral consequences:
                    - Some developers might prefer framework integrations for convenience.
                    
                    Negative consequences:
                    - Developers can no longer use framework annotations on application classes, which may introduce
                    more boilerplate code.
                    """);

    @ArchTest
    static final ArchRule NOTHING_SHOULD_DEPEND_ON_INFRASTRUCTURE = noClasses().that()
            .resideOutsideOfPackage(ArchitectureRules.Constants.INFRASTRUCTURE_PACKAGE_PATTERN)
            .should().dependOnClassesThat()
            .resideInAPackage(ArchitectureRules.Constants.INFRASTRUCTURE_PACKAGE_PATTERN)
            .because("""
                    the infrastructure package should only contain plumbing necessary to run the application.
                    
                    Context:
                    Infrastructure code provides technical support for the application, \
                    such as database access, messaging, and external system integrations.
                    Allowing external dependencies on infrastructure code \
                    couples application logic to technical details, \
                    making modifications and refactoring more challenging.
                    By keeping infrastructure isolated, feature logic remains independent of technical concerns, \
                    improving flexibility and maintainability.
                    
                    Positive consequences:
                    - Improves maintainability by preventing business logic from depending on technical infrastructure.
                    - Enhances flexibility by allowing infrastructure changes without impacting application logic.
                    - Makes it easier to reason about the application,
                      as the application code remains free of technical details.
                    
                    Negative consequences:
                    - Adds some complexity due to requiring an additional layer of abstraction
                    """);

    @ArchTest
    static final ArchRule ONLY_REPOSITORY_IMPLEMENTATIONS_SHOULD_DIRECTLY_CALL_SPRING_REPOSITORIES = noClasses().that()
            .resideOutsideOfPackages(ArchitectureRules.Constants.PERSISTENCE_ADAPTER_PACKAGE_PATTERN,
                    ArchitectureRules.Constants.CONFIG_PACKAGE_PATTERN)
            .should().dependOnClassesThat().areAssignableTo(Repository.class)
            .orShould().accessClassesThat().areAssignableTo(Repository.class)
            .because("""
                    Spring repositories are an implementation detail of repositories.
                    
                    Context:
                    Spring repositories offer convenient data access patterns that developers commonly use directly
                    throughout applications. \
                    This reduces initial development time and is a familiar pattern for many developers.
                    
                    However, this direct usage also leads to persistence implementation details spreading \
                    across the codebase, including business logic layers. \
                    When persistence requirements change, modifications ripple through
                    multiple parts of the application instead of being contained to a single layer.
                    
                    Positive consequences:
                    - Maintains a clear separation of concerns
                    - Aligns with DDD principles.
                    - Aligns with Ports & Adapters (Hexagonal) architecture.
                    - Improves modularity and flexibility in changing persistence implementations.
                    
                    Negative consequences:
                    - Developers may be used to interacting directly with Spring repositories, \
                    requiring a shift in habits and mindset
                    - Adds some complexity due to requiring an additional layer of abstraction.
                    """);
}
