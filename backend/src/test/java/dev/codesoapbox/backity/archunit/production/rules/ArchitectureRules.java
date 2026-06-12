package dev.codesoapbox.backity.archunit.production.rules;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchTests;
import dev.codesoapbox.backity.BackityApplication;
import dev.codesoapbox.backity.archunit.production.rules.architecture.BasicPortsAndAdaptersArchitectureRules;
import dev.codesoapbox.backity.archunit.production.rules.architecture.DependencyRules;
import dev.codesoapbox.backity.archunit.production.rules.architecture.PackagingRules;
import dev.codesoapbox.backity.archunit.production.rules.architecture.SpringUsageRules;
import lombok.NoArgsConstructor;

/// Rules for enforcing architecture
@SuppressWarnings("unused")
public class ArchitectureRules {

    @ArchTest
    static final ArchTests BASIC_PORTS_AND_ADAPTERS = ArchTests.in(BasicPortsAndAdaptersArchitectureRules.class);

    @ArchTest
    static final ArchTests PACKAGING = ArchTests.in(PackagingRules.class);

    @ArchTest
    static final ArchTests DEPENDENCIES = ArchTests.in(DependencyRules.class);

    @ArchTest
    static final ArchTests SPRING_USAGE = ArchTests.in(SpringUsageRules.class);

    @NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static final class Constants {

        public static final String DOMAIN_LAYER = "Domain";
        public static final String DOMAIN_PACKAGE_NAME = "domain";
        public static final String DOMAIN_PACKAGE_PATTERN = ".." + DOMAIN_PACKAGE_NAME
                + "..";

        public static final String ADAPTER_LAYER = "Adapters";
        public static final String ADAPTERS_PACKAGE_NAME = "adapters";

        public static final String CONFIG_LAYER = "Config";
        public static final String CONFIG_PACKAGE_NAME = "config";
        public static final String CONFIG_PACKAGE_PATTERN = "..infrastructure."
                + CONFIG_PACKAGE_NAME
                + "..";

        public static final String APPLICATION_LAYER = "Application";
        public static final String APPLICATION_PACKAGE_NAME = "application";
        public static final String APPLICATION_PACKAGE_PATTERN = ".."
                + APPLICATION_PACKAGE_NAME + "..";

        public static final String EXCEPTIONS_PACKAGE_PATTERN = "..exceptions..";

        public static final String PERSISTENCE_ADAPTER_PACKAGE_PATTERN = ".." +
                ADAPTERS_PACKAGE_NAME +
                ".driven.persistence..";

        public static final String CONTROLLER_PACKAGE_PATTERN = ".." +
                ADAPTERS_PACKAGE_NAME +
                ".driving.api.http.controllers..";

        public static final String SPRING_PACKAGE_PATTERN = "org.springframework..";

        public static final String GAME_PROVIDERS_PACKAGE_PATTERN =
                BackityApplication.class.getPackageName() + ".gameproviders.(*)..";
        public static final String GAME_PROVIDERS_SHARED_PACKAGE_PATTERN =
                BackityApplication.class.getPackageName() + ".gameproviders.shared..";

        public static final String INFRASTRUCTURE_PACKAGE_PATTERN =
                BackityApplication.class.getPackageName() + ".infrastructure.(*)..";
    }
}
