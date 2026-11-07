package dev.codesoapbox.backity.archunit.combined;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchTests;
import com.tngtech.archunit.junit.CacheMode;
import dev.codesoapbox.backity.BackityApplication;
import dev.codesoapbox.backity.archunit.combined.rules.ProductionAndTestEncapsulationRules;

@SuppressWarnings("unused")
@AnalyzeClasses(packagesOf = BackityApplication.class, importOptions = ImportOption.DoNotIncludeArchives.class,
        // https://www.archunit.org/userguide/html/000_Index.html#_controlling_the_cache
        cacheMode = CacheMode.PER_CLASS)
public class ArchUnitProductionAndTestCodeTest {

    @ArchTest
    static final ArchTests ENCAPSULATION = ArchTests.in(ProductionAndTestEncapsulationRules.class);
}
