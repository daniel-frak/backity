package dev.codesoapbox.backity;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchIgnore;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.dependencies.SliceRule;
import dev.codesoapbox.backity.testing.archunit.AdapterPackagesOnlyAccessedByTheirConfigCondition;
import dev.codesoapbox.backity.testing.archunit.AdaptersShouldNotDependOnOtherAdaptersCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.*;
import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;
import static com.tngtech.archunit.core.domain.properties.HasName.Predicates.nameEndingWith;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.LayeredArchitecture;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/*
Describes how packages relate to each other
 */
@AnalyzeClasses(packagesOf = BackityApplication.class, importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    private static final String DOMAIN_LAYER = "domain";
    private static final String ADAPTER_LAYER = "adapter";
    private static final String CONFIG_LAYER = "config";
    private static final String DOMAIN_PACKAGE = "..domain..";
    private static final String DOMAIN_MODEL_PACKAGE = "..domain..model..";
    private static final String DOMAIN_SERVICES_PACKAGE = "..domain..services..";
    private static final String DOMAIN_REPOSITORIES_PACKAGE = "..domain..repositories..";
    private static final String EXCEPTIONS_PACKAGE = "..exceptions..";
    static final String CONFIG_PACKAGE = "..config..";
    private static final String PERSISTENCE_ADAPTER_PACKAGE = "..adapters.driven.persistence..";
    private static final String CONTROLLER_PACKAGE = "..adapters.driving.api.http.controllers..";
    private static final String SPRING_PACKAGE = "org.springframework..";
    private static final String SPRING_DATA_PACKAGE = "org.springframework.data..";
    private static final LayeredArchitecture LAYERS = layeredArchitecture()
            .layer(DOMAIN_LAYER).definedBy(DOMAIN_PACKAGE)
            .layer(ADAPTER_LAYER).definedBy("..adapters..")
            .layer(CONFIG_LAYER).definedBy(CONFIG_PACKAGE);

    @ArchTest
    static final ArchRule adapterLayerShouldOnlyBeAccessedByConfigLayer = LAYERS
            .whereLayer(ADAPTER_LAYER).mayOnlyBeAccessedByLayers(CONFIG_LAYER);

    @ArchTest
    static final ArchRule configLayerShouldNotBeAccessedByAnyLayer = LAYERS
            .whereLayer(CONFIG_LAYER).mayNotBeAccessedByAnyLayer();

    @ArchTest
    static final SliceRule adapterPackagesShouldNotDependOnEachOther = slices()
            .matching("..adapters.(*)..").should().notDependOnEachOther();

    @ArchTest
    static final ArchRule adapterPackagesShouldOnlyBeAccessedByThemselvesOrTheirOwnConfig = classes().that()
            .resideInAPackage("..adapters.*..")
            .should(new AdapterPackagesOnlyAccessedByTheirConfigCondition(
                    "adapters", "config"));

    @ArchTest
    static final ArchRule adaptersShouldNotDependOnEachOther = classes().that()
            .resideInAPackage("..adapters.*..")
            .should(new AdaptersShouldNotDependOnOtherAdaptersCondition(BackityApplication.class.getPackageName(),
                    "domain"));

    @ArchTest
    static final ArchRule domainModelShouldNotCallServices = noClasses().that()
            .resideInAPackage(DOMAIN_MODEL_PACKAGE)
            .should().accessClassesThat().resideInAPackage(DOMAIN_SERVICES_PACKAGE);

    @ArchTest
    static final ArchRule domainRepositoriesShouldBeInterfaces = classes().that()
            .resideInAPackage(DOMAIN_REPOSITORIES_PACKAGE)
            .should().beInterfaces();

    @ArchTest
    static final ArchRule exceptionsShouldBeInCorrectPackage = classes().that()
            .areAssignableTo(Exception.class)
            .should().resideInAPackage(EXCEPTIONS_PACKAGE);

    /*
    This should be enabled in the future, once Spring Data dependencies are removed from the domain
     */
    @ArchIgnore
    @ArchTest
    static final ArchRule domainShouldNotDependOnSpring = noClasses().that()
            .resideInAPackage(DOMAIN_PACKAGE)
            .should().dependOnClassesThat(resideInAPackage(SPRING_PACKAGE));

    /*
    This should be removed in the future, once Spring Data dependencies are removed from the domain
     */
    @ArchTest
    static final ArchRule nonEntityDomainShouldNotDependOnSpringUnlessForPagination = noClasses().that()
            .resideInAPackage(DOMAIN_PACKAGE)
            .and(not(annotatedWith(Entity.class)))
            .and(not(nameEndingWith("Repository")))
            .should().dependOnClassesThat(resideInAPackage(SPRING_PACKAGE)
                    .and(not(equivalentTo(Page.class)))
                    .and(not(equivalentTo(PageImpl.class)))
                    .and(not(equivalentTo(Pageable.class))));

    /*
    This should be removed in the future, once Spring Data dependencies are removed from the domain
     */
    @ArchTest
    static final ArchRule domainEntitiesAndRepositoriesShouldNotDependOnSpringExceptForData = noClasses().that()
            .resideInAPackage(DOMAIN_PACKAGE)
            .and(simpleNameEndingWith("Foo").or(annotatedWith(Entity.class)))
            .should().dependOnClassesThat(resideInAPackage(SPRING_PACKAGE)
                    .and(not(resideInAPackage(SPRING_DATA_PACKAGE))));

    @ArchTest
    static final ArchRule repositoryImplementationsShouldResideInCorrectPackage = classes().that()
            .areNotInterfaces().and()
            .haveNameMatching(".*Repository")
            .should().resideInAPackage(PERSISTENCE_ADAPTER_PACKAGE);

    @ArchTest
    static final ArchRule onlyRepositoryImplementationsShouldDirectlyCallSpringRepositories = noClasses().that()
            .resideOutsideOfPackages(PERSISTENCE_ADAPTER_PACKAGE, CONFIG_PACKAGE)
            .should().dependOnClassesThat().areAssignableTo(Repository.class)
            .orShould().accessClassesThat().areAssignableTo(Repository.class);

    @ArchTest
    static final ArchRule controllersShouldResideInCorrectPackage = classes().that()
            .areAnnotatedWith(RestController.class)
            .should().resideInAPackage(CONTROLLER_PACKAGE);

    @ArchTest
    static final ArchRule coreShouldNotDependOnIntegrations = noClasses().that()
            .resideInAPackage(BackityApplication.class.getPackageName() + ".core..")
            .should().dependOnClassesThat()
            .resideInAPackage(BackityApplication.class.getPackageName() + ".integrations..");

    @ArchTest
    static final ArchRule integrationsShouldNotDependOnEachOther = slices()
            .matching(BackityApplication.class.getPackageName() + ".integrations.(*)..")
            .should().notDependOnEachOther();

}
