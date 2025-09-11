package dev.codesoapbox.backity.archunit.rules;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchTests;

public class ControllerRules {

    @ArchTest
    static final ArchTests OPENAPI_RULES = ArchTests.in(OpenApiControllerRules.class);
}
