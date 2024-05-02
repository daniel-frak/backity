package dev.codesoapbox.backity.archunit.rules;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import dev.codesoapbox.backity.BackityApplication;
import org.springframework.data.repository.Repository;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/*
Describes how packages relate to each other
 */
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

    @ArchTest
    static final ArchRule EXCEPTIONS_SHOULD_BE_IN_CORRECT_PACKAGE = classes().that()
            .areAssignableTo(Exception.class)
            .should().resideInAPackage(EXCEPTIONS_PACKAGE);

    @ArchTest
    static final ArchRule DOMAIN_SHOULD_NOT_DEPEND_ON_SPRING = noClasses().that()
            .resideInAPackage(DOMAIN_PACKAGE)
            .should().dependOnClassesThat(resideInAPackage(SPRING_PACKAGE));

    @ArchTest
    static final ArchRule REPOSITORY_IMPLEMENTATIONS_SHOULD_RESIDE_IN_CORRECT_PACKAGE = classes().that()
            .areNotInterfaces().and()
            .haveNameMatching(".*Repository")
            .should().resideInAPackage(PERSISTENCE_ADAPTER_PACKAGE);

    @ArchTest
    static final ArchRule ONLY_REPOSITORY_IMPLEMENTATIONS_SHOULD_DIRECTLY_CALL_SPRING_REPOSITORIES = noClasses().that()
            .resideOutsideOfPackages(PERSISTENCE_ADAPTER_PACKAGE,
                    CONFIG_PACKAGE)
            .should().dependOnClassesThat().areAssignableTo(Repository.class)
            .orShould().accessClassesThat().areAssignableTo(Repository.class);

    @ArchTest
    static final ArchRule CONTROLLERS_SHOULD_RESIDE_IN_CORRECT_PACKAGE = classes().that()
            .areAnnotatedWith(RestController.class)
            .should().resideInAPackage(CONTROLLER_PACKAGE);

    @ArchTest
    static final ArchRule CORE_SHOULD_NOT_DEPEND_ON_INTEGRATIONS = noClasses().that()
            .resideInAPackage(BackityApplication.class.getPackageName() + ".core..")
            .should().dependOnClassesThat()
            .resideInAPackage(BackityApplication.class.getPackageName() + ".integrations..");

    @ArchTest
    static final ArchRule INTEGRATIONS_SHOULD_NOT_DEPEND_ON_EACH_OTHER = slices()
            .matching(BackityApplication.class.getPackageName() + ".integrations.(*)..")
            .should().notDependOnEachOther();

}
