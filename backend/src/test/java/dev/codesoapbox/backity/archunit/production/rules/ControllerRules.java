package dev.codesoapbox.backity.archunit.production.rules;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchTests;
import dev.codesoapbox.backity.archunit.production.rules.controller.OpenApiControllerRules;

/// Rules specifically about controllers, unrelated to anything else.
@SuppressWarnings("unused")
public class ControllerRules {

    @ArchTest
    static final ArchTests OPENAPI = ArchTests.in(OpenApiControllerRules.class);
}
