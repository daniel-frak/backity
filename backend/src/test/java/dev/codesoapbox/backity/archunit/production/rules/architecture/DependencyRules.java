package dev.codesoapbox.backity.archunit.production.rules.architecture;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchTests;
import dev.codesoapbox.backity.archunit.production.rules.architecture.dependencies.ControllerBoundaryRules;
import dev.codesoapbox.backity.archunit.production.rules.architecture.dependencies.InfrastructureIsolationRules;
import dev.codesoapbox.backity.archunit.production.rules.architecture.dependencies.ModuleIsolationRules;

/// Rules about how modules and layers should depend on each other.
@SuppressWarnings("unused")
public class DependencyRules {

    @ArchTest
    static final ArchTests INFRASTRUCTURE_ISOLATION_RULES = ArchTests.in(InfrastructureIsolationRules.class);

    @ArchTest
    static final ArchTests MODULE_ISOLATION_RULES = ArchTests.in(ModuleIsolationRules.class);

    @ArchTest
    static final ArchTests CONTROLLER_BOUNDARY_RULES = ArchTests.in(ControllerBoundaryRules.class);
}
