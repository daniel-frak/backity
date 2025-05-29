package dev.codesoapbox.backity.archunit.rules;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.dependencies.SliceRule;
import dev.codesoapbox.backity.BackityApplication;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static dev.codesoapbox.backity.archunit.rules.PortsAndAdaptersArchitectureRules.Constants.*;

/*
Enforces a Ports & Adapters package structure
 */
@SuppressWarnings("unused")
public final class PortsAndAdaptersArchitectureRules {

    @ArchTest
    static final ArchRule LAYER_ACCESS_RULES_SHOULD_BE_RESPECTED = layeredArchitecture()
            .consideringOnlyDependenciesInAnyPackage(
                    BackityApplication.class.getPackageName() + "..")
            .layer(DOMAIN_LAYER).definedBy(".." + DOMAIN_PACKAGE + "..")
            .layer(ADAPTER_LAYER).definedBy(".." + ADAPTERS_PACKAGE + "..")
            .layer(CONFIG_LAYER).definedBy(".." + CONFIG_PACKAGE + "..")
            .layer(APPLICATION_LAYER).definedBy(".." + APPLICATION_PACKAGE + "..")
            .whereLayer(ADAPTER_LAYER).mayOnlyBeAccessedByLayers(CONFIG_LAYER)
            .whereLayer(CONFIG_LAYER).mayNotBeAccessedByAnyLayer()
            .whereLayer(APPLICATION_LAYER).mayOnlyBeAccessedByLayers(ADAPTER_LAYER, CONFIG_LAYER);

    // @TODO Rename to ADAPTER_TYPES_SHOULD_NOT_DEPEND_ON_EACH_OTHER?
    @ArchTest
    static final SliceRule ADAPTERS_SHOULD_NOT_DEPEND_ON_EACH_OTHER = slices()
            .matching(".." + ADAPTERS_PACKAGE + ".*.(*)..").should().notDependOnEachOther();

    private PortsAndAdaptersArchitectureRules() {
    }

    static final class Constants {

        static final String DOMAIN_LAYER = "Domain";
        static final String ADAPTER_LAYER = "Adapters";
        static final String CONFIG_LAYER = "Config";
        static final String APPLICATION_LAYER = "Application";
        static final String DOMAIN_PACKAGE = "domain";
        static final String ADAPTERS_PACKAGE = "adapters";
        static final String CONFIG_PACKAGE = "config";
        static final String APPLICATION_PACKAGE = "application";

        private Constants() {
        }
    }
}