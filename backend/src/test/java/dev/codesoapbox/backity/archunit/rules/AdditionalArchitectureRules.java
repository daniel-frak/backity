package dev.codesoapbox.backity.archunit.rules;

import com.tngtech.archunit.core.domain.*;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import dev.codesoapbox.backity.BackityApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/*
Describes how packages relate to each other, where the standard Ports & Adapters rules are not specific enough.
 */
@SuppressWarnings("unused")
public class AdditionalArchitectureRules {

    static final String EXCEPTIONS_PACKAGE = "..exceptions..";
    static final String PERSISTENCE_ADAPTER_PACKAGE = ".." +
            PortsAndAdaptersArchitectureRules.Constants.ADAPTERS_PACKAGE +
            ".driven.persistence..";
    static final String CONTROLLER_PACKAGE = ".." +
            PortsAndAdaptersArchitectureRules.Constants.ADAPTERS_PACKAGE +
            ".driving.api.http.controllers..";
    static final String SPRING_PACKAGE = "org.springframework..";
    static final String DOMAIN_PACKAGE = ".." + PortsAndAdaptersArchitectureRules.Constants.DOMAIN_PACKAGE
            + "..";
    static final String CONFIG_PACKAGE = "..infrastructure."
            + PortsAndAdaptersArchitectureRules.Constants.CONFIG_PACKAGE
            + "..";
    static final String APPLICATION_PACKAGE = ".."
            + PortsAndAdaptersArchitectureRules.Constants.APPLICATION_PACKAGE + "..";
    static final String GAME_PROVIDERS_PACKAGE =
            BackityApplication.class.getPackageName() + ".gameproviders.(*)..";
    static final String GAME_PROVIDERS_SHARED_PACKAGE =
            BackityApplication.class.getPackageName() + ".gameproviders.shared..";
    static final String INFRASTRUCTURE_PACKAGE =
            BackityApplication.class.getPackageName() + ".infrastructure.(*)..";

    @ArchTest
    static final ArchRule EXCEPTIONS_SHOULD_BE_IN_CORRECT_PACKAGE = classes().that()
            .areAssignableTo(Exception.class)
            .should().resideInAPackage(EXCEPTIONS_PACKAGE)
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
    static final ArchRule CONFIGURATION_SHOULD_RESIDE_IN_CORRECT_PACKAGE = classes().that()
            .areAnnotatedWith(Configuration.class)
            .should().resideInAPackage(CONFIG_PACKAGE)
            .because("""
                    a consistent structure makes code easier to work with.
                    
                    Context:
                    If configuration classes are scattered across the project, developers \
                    may struggle to find and use them properly. This can lead to duplicated and messy code.
                    Keeping them in a dedicated package makes it easier to manage and understand them.
                    
                    Positive consequences:
                    - Makes configuration classes easier to locate and manage.
                    - Reduces confusion by maintaining a consistent project structure.
                    
                    Neutral consequences:
                    - Configuration classes will still be spread across feature packages.
                    
                    Negative consequences:
                    - Can introduce unnecessary subpackage complexity in simple modules.
                    """);

    @ArchTest
    static final ArchRule DOMAIN_SHOULD_NOT_DEPEND_ON_SPRING = noClasses().that()
            .resideInAPackage(DOMAIN_PACKAGE)
            .should().dependOnClassesThat(resideInAPackage(SPRING_PACKAGE))
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
            .resideInAPackage(APPLICATION_PACKAGE)
            .should().dependOnClassesThat(resideInAPackage(SPRING_PACKAGE))
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
    static final ArchRule REPOSITORY_IMPLEMENTATIONS_SHOULD_RESIDE_IN_CORRECT_PACKAGE = classes().that()
            .areNotInterfaces().and()
            .haveNameMatching(".*Repository")
            .should().resideInAPackage(PERSISTENCE_ADAPTER_PACKAGE)
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
    static final ArchRule ONLY_REPOSITORY_IMPLEMENTATIONS_SHOULD_DIRECTLY_CALL_SPRING_REPOSITORIES = noClasses().that()
            .resideOutsideOfPackages(PERSISTENCE_ADAPTER_PACKAGE,
                    CONFIG_PACKAGE)
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

    @ArchTest
    static final ArchRule CONTROLLERS_SHOULD_RESIDE_IN_CORRECT_PACKAGE = classes().that()
            .areAnnotatedWith(RestController.class)
            .should().resideInAPackage(CONTROLLER_PACKAGE)
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
    static final ArchRule CONTROLLERS_SHOULD_NOT_HAVE_DOMAIN_CLASSES_AS_PROPERTIES =
            ArchRuleDefinition.classes().that()
                    .haveSimpleNameEndingWith("Controller")
                    .should(new ArchCondition<>("not have domain classes as properties") {
                        @Override
                        public void check(JavaClass javaClass, ConditionEvents events) {
                            for (JavaField field : javaClass.getFields()) {
                                if (field.getRawType().getPackageName().contains("domain")) {
                                    String message = String.format(
                                            "Class %s has a field %s of type %s, which is in the domain package",
                                            javaClass.getName(), field.getName(), field.getRawType().getName());
                                    events.add(SimpleConditionEvent.violated(field, message));
                                }
                            }
                        }
                    }).because("""
                            controllers should interact with the domain through application services.
                            
                            Context:
                            In Ports & Adapters architecture, controllers act as driving adapters that \
                            interact with application ports. \
                            In Use-Case Driven design, Use Case classes define application-level interactions \
                            and encapsulate business workflows, making it easier to reason about application functionality.
                            Because Use Cases expose well-defined application operations, \
                            they also serve as natural ports for driving adapters.
                            While directly calling domain services simplifies development, \
                            it weakens the use-case-driven approach \
                            and increases the likelihood of complex logic appearing in controllers.
                            This can lead to code duplication if multiple driving adapters need to invoke the same use case.
                            
                            Positive consequences:
                            - Reduces the likelihood of controllers accumulating unnecessary complexity.
                            - Reduces the likelihood of duplication in driving adapters.
                            - Aligns with Ports & Adapters architecture (with Use Case classes as ports).
                            - Improves adaptability when evolving domain models or replacing delivery mechanisms.
                            
                            Negative consequences:
                            - Developers accustomed to direct domain service calls may need to adjust their approach.
                            - Requires an extra layer of abstraction, which adds complexity.
                            """);

    @ArchTest
    static final ArchRule CONTROLLER_METHODS_SHOULD_NOT_RETURN_DOMAIN_CLASSES = methods()
            .that().areDeclaredInClassesThat().haveSimpleNameEndingWith("Controller")
            .and().arePublic()
            .should(new ArchCondition<>("not return domain classes") {
                @Override
                public void check(JavaMethod javaMethod, ConditionEvents events) {
                    JavaClass returnType = javaMethod.getRawReturnType();
                    List<JavaClass> domainDependencies = findDomainDependenciesInMethodReturn(returnType);
                    if (!domainDependencies.isEmpty()) {
                        addDependencyInMethodReturnViolation(javaMethod, events, domainDependencies, returnType);
                    }
                }

                private List<JavaClass> findDomainDependenciesInMethodReturn(JavaClass returnType) {
                    Set<JavaClass> transitiveDependencies = getAllTransitiveDependenciesInMethodReturn(returnType);

                    return transitiveDependencies.stream()
                            .filter(this::isDomainClass)
                            .toList();
                }

                private boolean isDomainClass(JavaClass clazz) {
                    return clazz.getPackageName().
                            contains(PortsAndAdaptersArchitectureRules.Constants.DOMAIN_PACKAGE);
                }

                private Set<JavaClass> getAllTransitiveDependenciesInMethodReturn(JavaClass returnType) {
                    return returnType.getTransitiveDependenciesFromSelf().stream()
                            .map(Dependency::getTargetClass)
                            .collect(Collectors.toSet());
                }

                private void addDependencyInMethodReturnViolation(
                        JavaMethod javaMethod, ConditionEvents events, List<JavaClass> domainDependencies,
                        JavaClass returnType) {
                    String message = createDependencyInMethodReturnViolationMessage(
                            javaMethod, domainDependencies, returnType);
                    events.add(SimpleConditionEvent.violated(javaMethod, message));
                }

                private String createDependencyInMethodReturnViolationMessage(
                        JavaMethod javaMethod, List<JavaClass> domainDependencies, JavaClass returnType) {
                    String message;
                    if (domainDependencies.contains(returnType)) {
                        message = String.format("%s returns %s which is a domain class",
                                javaMethod.getFullName(), returnType.getName());
                    } else {
                        var domainDependenciesString = domainDependencies.stream()
                                .map(JavaClass::getFullName)
                                .collect(Collectors.joining("%n - "));
                        message = String.format(
                                "%s returns %s which depends on the following domain classes:%n - %s",
                                javaMethod.getFullName(), returnType.getName(), domainDependenciesString);
                    }
                    return message;
                }
            }).because("""
                    small changes to the domain should not affect the API.
                    
                    Context:
                    Returning domain classes in controllers couples the API to internal business logic, \
                    making changes in domain models impact external consumers. \
                    This approach risks exposing internal implementation details, \
                    and entangling domain models with controller-specific code, \
                    making refactoring more difficult and reducing flexibility in evolving the domain model.
                    
                    DTOs separate domain models from API contracts, ensuring that modifications to domain models \
                    do not propagate unexpected changes to API contracts, and vice versa.
                    
                    Positive consequences:
                    - Strengthens separation of concerns between the public API and domain logic, \
                    improving maintainability.
                    - Reduces the risk of breaking the API when domain models evolve.
                    - Provides flexibility in shaping API responses independent of domain implementation details.
                    
                    Negative consequences:
                    - Requires explicit mapping between domain objects and DTOs, adding development overhead.
                    """);

    @ArchTest
    static final ArchRule CONTROLLER_METHODS_SHOULD_NOT_ACCEPT_DOMAIN_CLASSES = methods()
            .that().areDeclaredInClassesThat().haveSimpleNameEndingWith("Controller")
            .and().arePublic()
            .should(new ArchCondition<>("not accept domain classes as arguments") {
                @Override
                public void check(JavaMethod javaMethod, ConditionEvents events) {
                    List<JavaParameter> parameters = javaMethod.getParameters();
                    List<JavaClass> directDomainDependencies =
                            checkDirectDependencyViolationsInArguments(javaMethod, events, parameters);
                    checkTransitiveDependencyViolationsInArguments(
                            javaMethod, events, parameters, directDomainDependencies);
                }

                private List<JavaClass> checkDirectDependencyViolationsInArguments(
                        JavaMethod javaMethod, ConditionEvents events, List<JavaParameter> parameters) {
                    List<JavaClass> directDomainDependencies = findDirectDomainDependenciesInArguments(parameters);

                    if (!directDomainDependencies.isEmpty()) {
                        addDirectDependencyViolationInArguments(javaMethod, events, directDomainDependencies);
                    }
                    return directDomainDependencies;
                }

                private List<JavaClass> findDirectDomainDependenciesInArguments(List<JavaParameter> parameters) {
                    return parameters.stream()
                            .map(p -> p.getType().toErasure())
                            .filter(this::isDomainClass)
                            .toList();
                }

                private boolean isDomainClass(JavaClass type) {
                    return type.getPackageName()
                            .contains(PortsAndAdaptersArchitectureRules.Constants.DOMAIN_PACKAGE);
                }

                private void addDirectDependencyViolationInArguments(
                        JavaMethod javaMethod, ConditionEvents events, List<JavaClass> directDomainDependencies) {
                    String message = String.format("%s accepts one or more domain classes as arguments:%n - %s",
                            javaMethod.getFullName(),
                            directDomainDependencies.stream()
                                    .map(JavaClass::getName)
                                    .collect(Collectors.joining("%n - ")));
                    events.add(SimpleConditionEvent.violated(javaMethod, message));
                }

                private void checkTransitiveDependencyViolationsInArguments(
                        JavaMethod javaMethod, ConditionEvents events, List<JavaParameter> parameters,
                        List<JavaClass> directDomainDependencies) {
                    Map<JavaClass, List<JavaClass>> transitiveDomainDependenciesByArgument =
                            findTransitiveDomainDependenciesByArgument(parameters, directDomainDependencies);
                    for (Map.Entry<JavaClass, List<JavaClass>> entry :
                            transitiveDomainDependenciesByArgument.entrySet()) {
                        if (entry.getValue().isEmpty()) {
                            continue;
                        }
                        addTransitiveDependencyInArgumentsViolation(javaMethod, events, entry);
                    }
                }

                private Map<JavaClass, List<JavaClass>> findTransitiveDomainDependenciesByArgument(
                        List<JavaParameter> parameters, List<JavaClass> directDomainDependencies) {
                    return parameters.stream()
                            .filter(p -> !directDomainDependencies.contains(p.getType().toErasure()))
                            .map(argumentParameter -> argumentParameter.getType().toErasure())
                            .collect(Collectors.toMap(c -> c, p -> p.getTransitiveDependenciesFromSelf().stream()
                                    .map(Dependency::getTargetClass)
                                    .distinct()
                                    .filter(this::isDomainClass)
                                    .toList()));
                }

                private void addTransitiveDependencyInArgumentsViolation(
                        JavaMethod javaMethod, ConditionEvents events, Map.Entry<JavaClass, List<JavaClass>> entry) {
                    String message = String.format(
                            "%s accepts class %s as argument which transitively depends on the following" +
                                    " domain classes:%n - %s",
                            javaMethod.getFullName(),
                            entry.getKey().getFullName(),
                            entry.getValue().stream()
                                    .map(JavaClass::getName)
                                    .collect(Collectors.joining("%n - ")));
                    events.add(SimpleConditionEvent.violated(javaMethod, message));
                }
            }).because("""
                    small changes to the domain should not affect the API.
                    
                    Context:
                    Accepting domain classes as arguments in controllers couples the API to internal business logic, \
                    making changes in domain models impact external consumers.
                    This approach risks exposing internal implementation details \
                    and entangling domain models with controller-specific code, \
                    making refactoring more difficult and reducing flexibility in evolving the domain model.
                    
                    DTOs separate domain models from API contracts, ensuring that modifications to domain models \
                    do not propagate unexpected changes to API contracts, and vice versa.
                    
                    Positive consequences:
                    - Strengthens separation of concerns between the public API and domain logic, \
                    improving maintainability.
                    - Reduces the risk of breaking the API when domain models evolve.
                    - Provides flexibility in shaping API responses independent of domain implementation details.
                    
                    Negative consequences:
                    - Requires explicit mapping between domain objects and DTOs, adding development overhead.
                    """);

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
    static final ArchRule GAME_PROVIDER_INTEGRATIONS_SHOULD_NOT_DEPEND_ON_EACH_OTHER = slices()
            .matching(GAME_PROVIDERS_PACKAGE)
            .should().notDependOnEachOther()
            .ignoreDependency(
                    resideInAnyPackage(GAME_PROVIDERS_PACKAGE), resideInAPackage(GAME_PROVIDERS_SHARED_PACKAGE))
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

    @ArchTest
    static final ArchRule NOTHING_SHOULD_NOT_DEPEND_ON_INFRASTRUCTURE = noClasses().that()
            .resideOutsideOfPackage(INFRASTRUCTURE_PACKAGE)
            .should().dependOnClassesThat()
            .resideInAPackage(INFRASTRUCTURE_PACKAGE)
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
    static final ArchRule SPRING_CONFIGURATION_ANNOTATION_SHOULD_NOT_BE_USED_DIRECTLY = noClasses()
            .that().areNotInterfaces()
            .should().beAnnotatedWith(Configuration.class)
            .because("""
                    using the @Configuration annotation only via meta-annotations provides clarity \
                    and makes slice testing easier.
                    
                    Context:
                    When using @Configuration classes through meta-annotations, these meta-annotations can be used \
                    to provide context around where the configured beans fit within the architecture of the application.
                    Additionally, these meta-annotations can be used to automatically configure slice tests.
                    
                    Positive consequences:
                    - Improved clarity due to meta-annotations providing context for the beans.
                    - Auto-configuring slice tests is possible due to meta-annotations on which @ComponentScan can rely.
                    
                    Negative consequences:
                    - Slightly more ceremony when defining bean factories.
                    """);
}
