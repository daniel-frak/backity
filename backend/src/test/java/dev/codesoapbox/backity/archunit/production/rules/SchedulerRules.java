package dev.codesoapbox.backity.archunit.production.rules;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.scheduling.annotation.Scheduled;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

/// Rules specifically about schedulers, unrelated to anything else.
@SuppressWarnings("unused")
public class SchedulerRules {

    @ArchTest
    static final ArchRule SPRING_SCHEDULERS_SHOULD_FOLLOW_NAMING_CONVENTION = classes().that(
                    new DescribedPredicate<>("have a @Scheduled method") {
                        @Override
                        public boolean test(JavaClass javaClass) {
                            return javaClass.getMethods().stream()
                                    .anyMatch(method -> method.isAnnotatedWith(Scheduled.class));
                        }
                    }
            )
            .should()
            .haveSimpleNameEndingWith("SpringScheduler")
            .because("""
                    a consistent structure makes code easier to work with.
                    
                    Context:
                    A shared `<Purpose><Technology>Scheduler` naming convention makes scheduled jobs easy to identify, \
                    search for, and distinguish from other components.
                    
                    Positive consequences:
                    - Scheduled jobs are immediately recognizable from their class names.
                    - Developers can quickly locate all scheduler implementations using IDE or repository searches.
                    
                    Negative consequences:
                    - Slightly stricter code requirements.
                    """);

    @ArchTest
    static final ArchRule SCHEDULERS_SHOULD_ONLY_HAVE_EXECUTE_METHOD = methods().that()
            .areDeclaredInClassesThat()
            .haveSimpleNameEndingWith("Scheduler")
            .and().arePublic()
            .should()
            .haveName("execute")
            .because("""
                    the scheduler should be adequately described by its class name.
                    
                    Context:
                    Scheduler classes should follow the `<Action><Technology>Scheduler` naming convention \
                    and contain only one method. \
                    Therefore, providing a method name other than `execute` would be superfluous and may lead to \
                    class and methods diverging over time.
                    
                    Positive consequences:
                    - Scheduler naming is more likely to be up-to-date due to scheduler class and method names \
                    not being able to diverge over time.
                    - Simple, predictable interface for all schedulers.
                    
                    Negative consequences:
                    - Slightly stricter code requirements.
                    """);
}
