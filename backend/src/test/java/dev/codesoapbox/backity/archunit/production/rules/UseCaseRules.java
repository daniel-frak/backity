package dev.codesoapbox.backity.archunit.production.rules;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

/// Rules specifically about use cases, unrelated to anything else.
@SuppressWarnings("unused")
public class UseCaseRules {

    @ArchTest
    static final ArchRule USE_CASES_SHOULD_ONLY_HAVE_EXECUTE_METHOD = methods().that()
            .areDeclaredInClassesThat()
            .haveSimpleNameEndingWith("UseCase")
            .and().arePublic()
            .should()
            .haveName("execute")
            .because("""
                    the use case should be adequately described by its class name.
                    
                    Context:
                    Use cases classes should follow the `<Action>UseCase` naming convention \
                    and contain only one method. \
                    Therefore, providing a method name other than `execute` would be superfluous and may lead to \
                    class and methods diverging over time.
                    
                    Positive consequences:
                    - Use case naming is more likely to be up-to-date due to use case class and method names \
                    not being able to diverge over time.
                    - Simple, predictable interface for all use cases.
                    
                    Negative consequences:
                    - Slightly stricter code requirements.
                    """);
}
