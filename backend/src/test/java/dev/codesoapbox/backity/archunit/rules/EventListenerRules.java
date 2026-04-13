package dev.codesoapbox.backity.archunit.rules;

import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.*;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

@SuppressWarnings("unused")
public class EventListenerRules {

    @ArchTest
    static final ArchRule TRANSACTIONAL_EVENT_LISTENER_MUST_HAVE_UNIQUE_ID = methods()
            .that().areAnnotatedWith(TransactionalEventListener.class)
            .should(new ArchCondition<>("have non-empty unique id") {

                private final Map<String, List<JavaMethod>> ids = new HashMap<>();

                @Override
                public void check(JavaMethod method, ConditionEvents events) {
                    Optional<TransactionalEventListener> annotationOpt =
                            method.tryGetAnnotationOfType(TransactionalEventListener.class);

                    if (annotationOpt.isEmpty()) {
                        return;
                    }

                    String id = annotationOpt
                            .map(TransactionalEventListener::id)
                            .map(Object::toString)
                            .orElse("");

                    if (id.trim().isEmpty()) {
                        events.add(SimpleConditionEvent.violated(
                                method,
                                "@TransactionalEventListener must declare a non-empty 'id'"
                        ));
                        return;
                    }

                    ids.computeIfAbsent(id, _ -> new ArrayList<>()).add(method);
                }

                @Override
                public void finish(ConditionEvents events) {
                    for (Map.Entry<String, List<JavaMethod>> entry : ids.entrySet()) {
                        if (entry.getValue().size() > 1) {
                            String id = entry.getKey();
                            List<JavaMethod> methods = entry.getValue();

                            for (JavaMethod method : methods) {
                                String location = method.getOwner().getName() + "#" + method.getName() + " " + method.getSourceCodeLocation();
                                String message = "Duplicate @TransactionalEventListener id '%s' used in: %s".formatted(
                                        id,
                                        location
                                );
                                events.add(SimpleConditionEvent.violated(method, message));
                            }
                        }
                    }
                    ids.clear();
                }
            })
            .allowEmptyShould(true)
            .because("""
                    it makes refactoring event listeners safer.
                    
                    Context:
                    By default, Spring Modulith saves full class names of event listeners in the database and \
                    relies on them when retrying failed events, which makes it much harder to refactor event handlers.
                    
                    Using a unique id for each event listener makes Spring save that id instead of the class name,
                    so that class name and package changes won't break event handling.
                    
                    Note that this DOES NOT cover domain event classes - those still require writing database migrations
                    for the event_publication#event_type column.
                    
                    Positive consequences:
                    - Allows changing listener class names and packages without breaking event handling.
                    
                    Negative consequences:
                    - Event handler definitions must be slightly more verbose.
                    """);

    @ArchTest
    static final ArchRule EVENT_LISTENERS_MUST_BE_ASYNC = methods()
            .that().areAnnotatedWith(EventListener.class)
            .should().beAnnotatedWith(Async.class)
            .because("""
                    event handlers must not be executed within the same transaction which triggered the event.
                    
                    Context:
                    We don't want to modify two aggregates in the same transaction.
                    Spring event listeners are synchronous by default and run within the same transaction as the
                    one in which the event was published. This would lead to event handlers being called within \
                    the same transaction as the event publisher, which would inevitably lead to multiple
                    aggregates being modified within the same transaction.
                    
                    Positive consequences:
                    - Prevents event handlers from being called within the same transaction as the event publisher.
                    
                    Negative consequences:
                    - Asynchronous operations are harder to reason about.
                    """
            );
}
