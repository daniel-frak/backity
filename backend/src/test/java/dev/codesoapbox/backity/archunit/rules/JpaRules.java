package dev.codesoapbox.backity.archunit.rules;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaAnnotation;
import com.tngtech.archunit.core.domain.JavaCall;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.properties.HasAnnotations;
import com.tngtech.archunit.core.domain.properties.HasName;
import com.tngtech.archunit.core.domain.properties.HasSourceCodeLocation;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import java.util.Locale;
import java.util.Optional;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.properties.HasName.Predicates.nameStartingWith;
import static com.tngtech.archunit.lang.conditions.ArchConditions.notBeAnnotatedWith;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

/**
 * Rules for JPA repositories
 */
@SuppressWarnings({"unused", "squid:S100"})
public final class JpaRules {

    private static final String TRANSACTIONAL_CLASS_NAME = "org.springframework.transaction.annotation.Transactional";
    private static final String JPA_REPOSITORY_CLASS_SUFFIX = "JpaRepository";

    @ArchTest
    static final ArchRule REPOSITORIES_SHOULD_BE_ANNOTATED_WITH_READ_ONLY_TRANSACTIONAL = classes()
            .that()
            .haveSimpleNameEndingWith(JPA_REPOSITORY_CLASS_SUFFIX)
            .should(BE_ANNOTATED_WITH_TRANSACTIONAL(true))
            .because("executing read-only queries in a read-only transaction can be more performant" +
                     " and use less memory (e.g., read-only entities are not dirty-checked," +
                     " persistent state snapshots are not being maintained for them) and sometimes save money" +
                     " (connecting to a read-only database instance may cost less). Since most repository methods" +
                     " will be queries, the default transaction should be read-only.");

    @ArchTest
    static final ArchRule MODIFYING_METHODS_SHOULD_BE_ANNOTATED_WITH_MODIFYING_TRANSACTIONAL = methods()
            .that()
            .areDeclaredInClassesThat()
            .haveSimpleNameEndingWith(JPA_REPOSITORY_CLASS_SUFFIX)
            .and().arePublic()
            .and(isModifyingMethod())
            .should(BE_ANNOTATED_WITH_TRANSACTIONAL(false))
            .because("modifying methods must make sure they are within a modifying transaction");

    @ArchTest
    static final ArchRule QUERY_METHODS_SHOULD_NOT_BE_ANNOTATED_WITH_TRANSACTIONAL = methods()
            .that()
            .areDeclaredInClassesThat()
            .haveSimpleNameEndingWith(JPA_REPOSITORY_CLASS_SUFFIX)
            .and().arePublic()
            .and(not(isModifyingMethod()))
            .should(notBeAnnotatedWith(TRANSACTIONAL_CLASS_NAME))
            .because("the containing class should already have the correct @Transactional annotation");

    private static final ArchCondition<JavaMethod> CALL_METHOD_WITH_NAME_CONTAINING_FLUSH =
            new ArchCondition<>("call flush()") {
                @Override
                public void check(JavaMethod item, ConditionEvents events) {
                    boolean callsFlush = item.getCallsFromSelf().stream()
                            .anyMatch(this::methodNameContainsCaseInsensitiveFlush);
                    if (!callsFlush) {
                        String message =
                                String.format("Method %s should call a method containing 'flush'", item.getFullName());
                        events.add(SimpleConditionEvent.violated(item, message));
                    }
                }

                private boolean methodNameContainsCaseInsensitiveFlush(JavaCall<?> call) {
                    return call.getTarget().getFullName().toLowerCase(Locale.ROOT).contains("flush");
                }
            };

    @ArchTest
    static final ArchRule DELETE_ALL_METHODS_SHOULD_FLUSH = methods()
            .that()
            .areDeclaredInClassesThat()
            .haveSimpleNameEndingWith(JPA_REPOSITORY_CLASS_SUFFIX)
            .and().arePublic()
            .and(isDeleteAllMethod())
            .should(CALL_METHOD_WITH_NAME_CONTAINING_FLUSH)
            .because("Hibernate may not behave correctly if the delete is part of a larger transaction")
            .allowEmptyShould(true); // There were no delete methods at the time of writing this rule

    private JpaRules() {
    }

    private static DescribedPredicate<HasName> isModifyingMethod() {
        return isSaveMethod()
                .or(isUpdateMethod())
                .or(isDeleteMethod());
    }

    private static DescribedPredicate<HasName> isSaveMethod() {
        return nameStartingWith("save")
                .or(nameStartingWith("persist"))
                .or(nameStartingWith("create"))
                .or(nameStartingWith("insert"))
                .or(nameStartingWith("add"));
    }

    private static DescribedPredicate<HasName> isUpdateMethod() {
        return nameStartingWith("update");
    }

    private static DescribedPredicate<HasName> isDeleteMethod() {
        return nameStartingWith("delete")
                .or(nameStartingWith("remove"));
    }

    private static DescribedPredicate<HasName> isDeleteAllMethod() {
        return nameStartingWith("deleteAll")
                .or(nameStartingWith("removeAll"));
    }

    @SuppressWarnings("java:S1188") // It seems a better idea to keep this as an internal class for now
    private static <T extends HasAnnotations<T>> ArchCondition<HasAnnotations<T>> BE_ANNOTATED_WITH_TRANSACTIONAL(
            boolean shouldBeReadOnly) {
        return new ArchCondition<>("be annotated with @Transactional(readOnly=" + shouldBeReadOnly + ")") {

            @Override
            public void check(HasAnnotations<T> item, ConditionEvents conditionEvents) {
                Optional<Boolean> isSpecificTransactional = isSpecificTransactional(item);

                if (isSpecificTransactional.isPresent() && Boolean.TRUE.equals(isSpecificTransactional.get())) {
                    return;
                }
                String message = isSpecificTransactional
                        .map(isReadOnly -> buildTransactionalIsReadOnlyMessage(item))
                        .orElseGet(() -> buildTransactionalIsMissingMessage(item));
                conditionEvents.add(SimpleConditionEvent.violated(item, message));
            }

            private Optional<Boolean> isSpecificTransactional(HasAnnotations<T> item) {
                return item.getAnnotations().stream()
                        .filter(annotation -> annotation.getRawType().isAssignableTo(TRANSACTIONAL_CLASS_NAME))
                        .findFirst()
                        .map(this::isSpecificReadOnly);
            }

            private boolean isSpecificReadOnly(JavaAnnotation<?> annotation) {
                return annotation.get("readOnly")
                        .map(Boolean.class::cast)
                        .map(isReadOnly -> shouldBeReadOnly == isReadOnly)
                        .orElse(false);
            }

            private String buildTransactionalIsReadOnlyMessage(HasAnnotations<T> item) {
                return String.format(
                        "%s is annotated with @Transactional(readOnly=%s)," +
                        " instead of @Transactional(readOnly=%s) in %s",
                        item.getDescription(), !shouldBeReadOnly, shouldBeReadOnly, getSourceCodeLocation(item));
            }

            /*
            It doesn't seem possible to have a generic ArchCondition for an item which inherits both HasAnnotations
            and HasSourceCodeLocation, so a cast must be made.
             */
            private String getSourceCodeLocation(HasAnnotations<T> item) {
                if (item instanceof HasSourceCodeLocation sourceCodeLocationItem) {
                    return sourceCodeLocationItem.getSourceCodeLocation().toString();
                }
                return "(unknown location)";
            }

            private String buildTransactionalIsMissingMessage(HasAnnotations<T> item) {
                return String.format(
                        "%s is not annotated with @Transactional(readOnly=%s) in %s",
                        item.getDescription(), shouldBeReadOnly, getSourceCodeLocation(item));
            }
        };
    }
}
