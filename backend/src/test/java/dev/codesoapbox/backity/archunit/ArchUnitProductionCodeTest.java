package dev.codesoapbox.backity.archunit;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchTests;
import com.tngtech.archunit.junit.CacheMode;
import dev.codesoapbox.backity.BackityApplication;
import dev.codesoapbox.backity.archunit.rules.AdditionalArchitectureRules;
import dev.codesoapbox.backity.archunit.rules.GeneralCodingRules;
import dev.codesoapbox.backity.archunit.rules.JpaRules;
import dev.codesoapbox.backity.archunit.rules.PortsAndAdaptersArchitectureRules;

@SuppressWarnings("unused")
@AnalyzeClasses(packagesOf = BackityApplication.class, importOptions = ImportOption.DoNotIncludeTests.class,
        // https://www.archunit.org/userguide/html/000_Index.html#_controlling_the_cache
        cacheMode = CacheMode.PER_CLASS)
public class ArchUnitProductionCodeTest {

    @ArchTest
    static final ArchTests ARCHITECTURE_RULES = ArchTests.in(PortsAndAdaptersArchitectureRules.class);

    @ArchTest
    static final ArchTests ADDITIONAL_ARCHITECTURE_RULES = ArchTests.in(AdditionalArchitectureRules.class);

    @ArchTest
    static final ArchTests CODING_RULES = ArchTests.in(GeneralCodingRules.class);

    @ArchTest
    static final ArchTests JPA_RULES = ArchTests.in(JpaRules.class);
}
