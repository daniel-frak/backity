package dev.codesoapbox.backity.archunit.production;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchTests;
import com.tngtech.archunit.junit.CacheMode;
import dev.codesoapbox.backity.BackityApplication;
import dev.codesoapbox.backity.archunit.production.rules.*;

@SuppressWarnings("unused")
@AnalyzeClasses(packagesOf = BackityApplication.class, importOptions = ImportOption.DoNotIncludeTests.class,
        // https://www.archunit.org/userguide/html/000_Index.html#_controlling_the_cache
        cacheMode = CacheMode.PER_CLASS)
public class ArchUnitProductionCodeTest {

    @ArchTest
    static final ArchTests LOW_LEVEL_CODING = ArchTests.in(LowLevelCodingRules.class);

    @ArchTest
    static final ArchTests LOW_LEVEL_NAMING = ArchTests.in(LowLevelNamingRules.class);

    @ArchTest
    static final ArchTests ARCHITECTURE = ArchTests.in(ArchitectureRules.class);

    @ArchTest
    static final ArchTests JPA = ArchTests.in(JpaRules.class);

    @ArchTest
    static final ArchTests CONTROLLERS = ArchTests.in(ControllerRules.class);

    @ArchTest
    static final ArchTests EVENT_LISTENERS = ArchTests.in(EventListenerRules.class);

    @ArchTest
    static final ArchTests SCHEDULERS = ArchTests.in(SchedulerRules.class);

    @ArchTest
    static final ArchTests USE_CASES = ArchTests.in(UseCaseRules.class);
}
