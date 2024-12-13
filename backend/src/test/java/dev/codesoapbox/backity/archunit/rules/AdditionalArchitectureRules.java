package dev.codesoapbox.backity.archunit.rules;

import com.tngtech.archunit.core.domain.*;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import dev.codesoapbox.backity.BackityApplication;
import org.springframework.data.repository.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/*
Describes how packages relate to each other, where the standard Ports & Adapters rules are not specific enough.
 */
@SuppressWarnings("unused")
public class AdditionalArchitectureRules {

    private static final String EXCEPTIONS_PACKAGE = "..exceptions..";
    private static final String PERSISTENCE_ADAPTER_PACKAGE = ".." +
            PortsAndAdaptersArchitectureRules.Constants.ADAPTERS_PACKAGE + ".driven.persistence..";
    private static final String CONTROLLER_PACKAGE = ".." +
            PortsAndAdaptersArchitectureRules.Constants.ADAPTERS_PACKAGE + ".driving.api.http.controllers..";
    private static final String SPRING_PACKAGE = "org.springframework..";
    private static final String DOMAIN_PACKAGE = ".." + PortsAndAdaptersArchitectureRules.Constants.DOMAIN_PACKAGE
            + "..";
    private static final String CONFIG_PACKAGE = ".." + PortsAndAdaptersArchitectureRules.Constants.CONFIG_PACKAGE
            + "..";
    private static final String APPLICATION_PACKAGE = ".."
            + PortsAndAdaptersArchitectureRules.Constants.APPLICATION_PACKAGE + "..";
    private static final String INTEGRATIONS_PACKAGE =
            BackityApplication.class.getPackageName() + ".integrations.(*)..";
    private static final String INFRASTRUCTURE_PACKAGE =
            BackityApplication.class.getPackageName() + ".infrastructure.(*)..";

    @ArchTest
    static final ArchRule EXCEPTIONS_SHOULD_BE_IN_CORRECT_PACKAGE = classes().that()
            .areAssignableTo(Exception.class)
            .should().resideInAPackage(EXCEPTIONS_PACKAGE)
            .because("a consistent structure makes code easier to work with");

    @ArchTest
    static final ArchRule DOMAIN_SHOULD_NOT_DEPEND_ON_SPRING = noClasses().that()
            .resideInAPackage(DOMAIN_PACKAGE)
            .should().dependOnClassesThat(resideInAPackage(SPRING_PACKAGE))
            .because("domain should not be polluted with infrastructural code");

    @ArchTest
    static final ArchRule REPOSITORY_IMPLEMENTATIONS_SHOULD_RESIDE_IN_CORRECT_PACKAGE = classes().that()
            .areNotInterfaces().and()
            .haveNameMatching(".*Repository")
            .should().resideInAPackage(PERSISTENCE_ADAPTER_PACKAGE)
            .because("a consistent structure makes code easier to work with");

    @ArchTest
    static final ArchRule ONLY_REPOSITORY_IMPLEMENTATIONS_SHOULD_DIRECTLY_CALL_SPRING_REPOSITORIES = noClasses().that()
            .resideOutsideOfPackages(PERSISTENCE_ADAPTER_PACKAGE,
                    CONFIG_PACKAGE)
            .should().dependOnClassesThat().areAssignableTo(Repository.class)
            .orShould().accessClassesThat().areAssignableTo(Repository.class)
            .because("Spring repositories are an implementation detail of repositories");

    @ArchTest
    static final ArchRule CONTROLLERS_SHOULD_RESIDE_IN_CORRECT_PACKAGE = classes().that()
            .areAnnotatedWith(RestController.class)
            .should().resideInAPackage(CONTROLLER_PACKAGE)
            .because("a consistent structure makes code easier to work with");

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
                    }).because("controllers should interact with the domain through application services");

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

                private String createDependencyInMethodReturnViolationMessage(JavaMethod javaMethod, List<JavaClass> domainDependencies, JavaClass returnType) {
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
            }).because("small changes to the domain should not affect the API");

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
            }).because("small changes to the domain should not affect the API");

    @ArchTest
    static final ArchRule CORE_SHOULD_NOT_DEPEND_ON_INTEGRATIONS = noClasses().that()
            .resideInAPackage(BackityApplication.class.getPackageName() + ".core..")
            .should().dependOnClassesThat()
            .resideInAPackage(BackityApplication.class.getPackageName() + ".integrations..")
            .because("making the core unaware of specific integrations will increase maintainability");

    @ArchTest
    static final ArchRule INTEGRATIONS_SHOULD_NOT_DEPEND_ON_EACH_OTHER = slices()
            .matching(INTEGRATIONS_PACKAGE)
            .should().notDependOnEachOther()
            .because("making integrations unaware of each other will increase maintainability");

    @ArchTest
    static final ArchRule NOTHING_SHOULD_NOT_DEPEND_ON_INFRASTRUCTURE = noClasses().that()
            .resideOutsideOfPackage(INFRASTRUCTURE_PACKAGE)
            .should().dependOnClassesThat()
            .resideInAPackage(INFRASTRUCTURE_PACKAGE)
            .because("the infrastructure package should only contain plumbing necessary to run the application");
}
