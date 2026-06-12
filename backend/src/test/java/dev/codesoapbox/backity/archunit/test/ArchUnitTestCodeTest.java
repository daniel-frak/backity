package dev.codesoapbox.backity.archunit.test;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchTests;
import com.tngtech.archunit.junit.CacheMode;
import dev.codesoapbox.backity.BackityApplication;
import dev.codesoapbox.backity.archunit.test.rules.LowLevelTestRules;
import dev.codesoapbox.backity.archunit.test.rules.TestReadabilityRules;

@SuppressWarnings("unused")
@AnalyzeClasses(packagesOf = BackityApplication.class, importOptions = ImportOption.OnlyIncludeTests.class,
        // https://www.archunit.org/userguide/html/000_Index.html#_controlling_the_cache
        cacheMode = CacheMode.PER_CLASS)
public class ArchUnitTestCodeTest {

    @ArchTest
    static final ArchTests LOW_LEVEL_RULES = ArchTests.in(LowLevelTestRules.class);

    @ArchTest
    static final ArchTests READABILITY = ArchTests.in(TestReadabilityRules.class);
}
