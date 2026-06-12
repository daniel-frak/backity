package dev.codesoapbox.backity.archunit.production.rules.architecture.dependencies;

import com.tngtech.archunit.core.domain.*;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import dev.codesoapbox.backity.archunit.production.rules.ArchitectureRules;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

/// Rules about how controllers should interact with other modules and layers.
@SuppressWarnings("unused")
public class ControllerBoundaryRules {

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
            .and().areNotStatic()
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
                    return clazz.getPackageName()
                            .contains(ArchitectureRules.Constants.DOMAIN_PACKAGE_NAME);
                }

                private Set<JavaClass> getAllTransitiveDependenciesInMethodReturn(JavaClass returnType) {
                    Set<JavaClass> visited = new HashSet<>();
                    walkNonStaticDependencies(returnType, visited);
                    visited.remove(returnType);
                    return visited;
                }

                private void walkNonStaticDependencies(JavaClass clazz, Set<JavaClass> visited) {
                    if (!visited.add(clazz)) {
                        return;
                    }
                    clazz.getDirectDependenciesFromSelf().stream()
                            .filter(this::isNotFromStaticMember)
                            .map(Dependency::getTargetClass)
                            .forEach(target -> walkNonStaticDependencies(target, visited));
                }

                private boolean isNotFromStaticMember(Dependency dependency) {
                    return dependency.getOriginClass()
                            .getAccessesFromSelf().stream()
                            .filter(access -> access.getTargetOwner().equals(dependency.getTargetClass()))
                            .anyMatch(access -> !access.getOrigin().getModifiers().contains(JavaModifier.STATIC));
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
                            .contains(ArchitectureRules.Constants.DOMAIN_PACKAGE_NAME);
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
                            .distinct()
                            .collect(Collectors.toMap(c -> c,
                                    p -> p.getTransitiveDependenciesFromSelf().stream()
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
}
