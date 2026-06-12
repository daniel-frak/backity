package dev.codesoapbox.backity.archunit.production.rules.architecture;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.dependencies.SliceRule;
import dev.codesoapbox.backity.BackityApplication;
import dev.codesoapbox.backity.archunit.production.rules.ArchitectureRules;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/// Rules for enforcing a basic Ports & Adapters package structure.
@SuppressWarnings("unused")
public class BasicPortsAndAdaptersArchitectureRules {

    @ArchTest
    static final ArchRule LAYER_ACCESS_RULES_SHOULD_BE_RESPECTED = layeredArchitecture()
            .consideringOnlyDependenciesInAnyPackage(
                    BackityApplication.class.getPackageName() + "..")
            .layer(ArchitectureRules.Constants.DOMAIN_LAYER).definedBy(".." + ArchitectureRules.Constants.DOMAIN_PACKAGE_NAME + "..")
            .layer(ArchitectureRules.Constants.ADAPTER_LAYER).definedBy(".." + ArchitectureRules.Constants.ADAPTERS_PACKAGE_NAME + "..")
            .layer(ArchitectureRules.Constants.CONFIG_LAYER).definedBy(".." + ArchitectureRules.Constants.CONFIG_PACKAGE_NAME + "..")
            .layer(ArchitectureRules.Constants.APPLICATION_LAYER).definedBy(".." + ArchitectureRules.Constants.APPLICATION_PACKAGE_NAME + "..")
            .whereLayer(ArchitectureRules.Constants.ADAPTER_LAYER).mayOnlyBeAccessedByLayers(ArchitectureRules.Constants.CONFIG_LAYER)
            .whereLayer(ArchitectureRules.Constants.CONFIG_LAYER).mayNotBeAccessedByAnyLayer()
            .whereLayer(ArchitectureRules.Constants.APPLICATION_LAYER).mayOnlyBeAccessedByLayers(ArchitectureRules.Constants.ADAPTER_LAYER, ArchitectureRules.Constants.CONFIG_LAYER);

    @ArchTest
    static final SliceRule ADAPTERS_SHOULD_NOT_DEPEND_ON_EACH_OTHER = slices()
            // Assumed package structure: *.adapters.driven/driving.adaptertype.(actualadapter)
            .matching(".." + ArchitectureRules.Constants.ADAPTERS_PACKAGE_NAME + ".*.*.(*)..").should().notDependOnEachOther();

    @ArchTest
    static final SliceRule ADAPTER_TYPES_SHOULD_NOT_DEPEND_ON_EACH_OTHER = slices()
            // Assumed package structure: *.adapters.driven/driving.(adaptertype)
            .matching(".." + ArchitectureRules.Constants.ADAPTERS_PACKAGE_NAME + ".*.(*)..").should().notDependOnEachOther();
}