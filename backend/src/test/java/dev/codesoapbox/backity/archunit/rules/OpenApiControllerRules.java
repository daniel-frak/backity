package dev.codesoapbox.backity.archunit.rules;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import dev.codesoapbox.backity.BackityApplication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

public final class OpenApiControllerRules {

    @ArchTest
    static final ArchRule OPENAPI_OPERATION_IDS_SHOULD_BE_UNIQUE =
            methods().that().arePublic().and().areDeclaredInClassesThat().areMetaAnnotatedWith(RestController.class)
                    .and().areMetaAnnotatedWith(RequestMapping.class)
                    .should(new HaveUniqueOpenApiOperationIdsCondition())
                    .because("""
                            non-unique operationIds may lead to issues when generating client code.
                            
                            Context:
                            Non-unique operationIds are likely to have a random suffix appended to them, \
                            and there is no guarantee that the same method will always get the same suffix.
                            This may lead to issues, especially when generating code from the OpenAPI documentation.
                            If an operationId is not explicitly defined via the \
                            @io.swagger.v3.oas.annotations.Operation annotation, \
                            OpenAPI generates one based on the method name.
                            
                            Positive consequences:
                            - The OpenAPI documentation will be more stable and readable, \
                            due to operationIds being unique.
                            
                            Negative consequences:
                            - Controller method names will be more verbose, \
                            unless the operationId is explicitly defined.
                            """);

    @ArchTest
    static final ArchRule HTTP_API_OBJECTS_SHOULD_HAVE_UNIQUE_SCHEMA_NAMES =
            classes().that(new PartOfHttpRequestOrResponsePredicate())
                    .should(new HaveUniqueSchemaNamesCondition())
                    .because("""
                non-unique schema names may lead to issues when generating client code.
                
                Context:
                Non-unique schema names in OpenAPI documentation are likely to produce invalid OpenAPI documentation, \
                as there cannot be more than
                one schema object with the same name.
                The tooling will most likely choose one of the duplicated schema objects at random and discard the rest.
                Schema names must be unique either through the class name or explicit @Schema annotation.
                
                If a schema object name is not explicitly defined via the \
                @io.swagger.v3.oas.annotations.Schema annotation, OpenAPI generates one based on the class name.
                
                Positive consequences:
                - Generated client code will be more stable and predictable.
                - OpenAPI documentation will be more clear and unambiguous.
                
                Negative consequences:
                - Class names may need to be more verbose, unless the schema name is explicitly defined.
                """);
    @ArchTest
    static final ArchRule operation_ids_must_include_controller_resource_name =
            methods().that().arePublic()
                    .and().areDeclaredInClassesThat().areMetaAnnotatedWith(RestController.class)
                    .and().areMetaAnnotatedWith(RequestMapping.class)
                    .should(new IncludeResourceNameInMethodCondition())
                    .because("""
                it helps avoid non-unique operationIds and makes the OpenAPI documentation more consistent..
                
                Context:
                OpenAPI operationIds must be unique across the application. An easy way to achieve this is to include the Controller resource name \
                in the method name.
                This also helps avoid an inconsistent API naming where some operationIds include a prefix or suffix for uniqueness, \
                while others don't.
                If an operationId is not explicitly defined via the @io.swagger.v3.oas.annotations.Operation annotation,
                OpenAPI generates one based on the method name.
                
                Positive consequences:
                - Risk of non-unique operationIds will be reduced.
                - OpenAPI documentation will be more consistent.
                
                Negative consequences:
                - OperationId names will need to be more verbose.
                """);

    private static class IncludeResourceNameInMethodCondition extends ArchCondition<JavaMethod> {

        IncludeResourceNameInMethodCondition() {
            super("include controller resource name in method name");
        }

        @Override
        public void check(JavaMethod method, ConditionEvents events) {
            String resourceName = getResourceName(method);

            if (resourceName.isBlank() || methodNameContainsResourceName(method, resourceName)) {
                return;
            }

            String message = buildViolationMessage(method, resourceName, method.getOwner().getSimpleName());
            events.add(SimpleConditionEvent.violated(method, message));
        }

        private String getResourceName(JavaMethod method) {
            JavaClass declaringClass = method.getOwner();
            String className = declaringClass.getSimpleName();
            return getResourceNameSingular(className);
        }

        private String getResourceNameSingular(String className) {
            return className
                    .replaceAll("(ReadController|Controller)$", "")
                    .replaceAll("(es|s)$", ""); // Remove plurality suffixes
        }

        private String buildViolationMessage(JavaMethod method, String resourceName, String className) {
            return String.format(
                    "Method '%s' in %s should include resource name '%s' derived from controller class '%s'",
                    method.getFullName(),
                    method.getSourceCodeLocation(),
                    resourceName,
                    className
            );
        }

        private boolean methodNameContainsResourceName(JavaMethod method, String resourceName) {
            String operationId = getOperationId(method);
            return operationId.toLowerCase().contains(resourceName.toLowerCase());
        }

        private String getOperationId(JavaMethod method) {
            return ArchUnitMetaAnnotation.tryGet(Operation.class, method.getAnnotations())
                    .filter(operation -> !operation.operationId().isBlank())
                    .map(Operation::operationId)
                    .orElseGet(method::getName);
        }
    }

    private static class HaveUniqueOpenApiOperationIdsCondition extends ArchCondition<JavaMethod> {

        private final Map<String, Set<JavaMethod>> operationIdsByMethods = new HashMap<>();

        HaveUniqueOpenApiOperationIdsCondition() {
            super("have unique OpenAPI operationIds");
        }

        private String getOperationId(JavaMethod method) {
            return ArchUnitMetaAnnotation.tryGet(Operation.class, method.getAnnotations())
                    .filter(operation -> !operation.operationId().isBlank())
                    .map(Operation::operationId)
                    .orElseGet(method::getName);
        }

        @Override
        public void check(JavaMethod method, ConditionEvents events) {
            String operationId = getOperationId(method);
            operationIdsByMethods.computeIfAbsent(operationId, k -> new HashSet<>()).add(method);
        }

        @Override
        public void finish(ConditionEvents events) {
            for (Map.Entry<String, Set<JavaMethod>> entry : operationIdsByMethods.entrySet()) {
                String operationId = entry.getKey();
                List<JavaMethod> methods = new ArrayList<>(entry.getValue());
                methods.sort(Comparator.comparing(JavaMethod::getFullName));
                if (methods.size() <= 1) {
                    continue;
                }

                JavaMethod representative = methods.getFirst();
                String message = buildMessage(operationId, methods);
                events.add(SimpleConditionEvent.violated(representative, message));
            }
        }

        private String buildMessage(String operationId, List<JavaMethod> methods) {
            String violatingMethodsList = getViolationLocationsAsString(methods);
            return String.format("OpenAPI operationId '%s' is not unique - used by:%n%s",
                    operationId, violatingMethodsList);
        }

        private String getViolationLocationsAsString(List<JavaMethod> methods) {
            return methods.stream()
                    .map(this::getViolationLocationAsString)
                    .collect(java.util.stream.Collectors.joining(System.lineSeparator()));
        }

        private String getViolationLocationAsString(JavaMethod method) {
            return "  - " + method.getFullName() + " in " + method.getSourceCodeLocation();
        }
    }

    private static class PartOfHttpRequestOrResponsePredicate extends DescribedPredicate<JavaClass> {

        public PartOfHttpRequestOrResponsePredicate() {
            super("are HTTP requests or responses");
        }

        @Override
        public boolean test(JavaClass javaClass) {
            Set<JavaClass> visited = new HashSet<>();
            return isUsedInRequestMapping(javaClass, visited);
        }

        private boolean isUsedInRequestMapping(JavaClass javaClass, Set<JavaClass> visited) {
            // Skip the class if it's not part of the application
            if(!javaClass.getPackageName().startsWith(BackityApplication.class.getPackageName() + ".")) {
                return false;
            }
            if (!visited.add(javaClass)) {
                return false;
            }

            return isDirectlyUsedInRequestMapping(javaClass)
                    || isIndirectlyUsedInRequestMapping(javaClass, visited);
        }

        private boolean isDirectlyUsedInRequestMapping(JavaClass javaClass) {
            return javaClass.getDirectDependenciesToSelf().stream()
                    .map(Dependency::getOriginClass)
                    .filter(originClass -> originClass.isMetaAnnotatedWith(RestController.class))
                    .flatMap(originClass -> originClass.getMethods().stream())
                    .anyMatch(method -> isRequestMappingUsingJavaClass(javaClass, method));
        }

        private boolean isRequestMappingUsingJavaClass(JavaClass javaClass, JavaMethod method) {
            return method.isMetaAnnotatedWith(RequestMapping.class)
                    && method.getAllInvolvedRawTypes().contains(javaClass);
        }

        private boolean isIndirectlyUsedInRequestMapping(JavaClass javaClass, Set<JavaClass> visited) {
            return javaClass.getDirectDependenciesToSelf().stream()
                    .filter(dependency -> dependency.getOriginClass().getFields().stream()
                            .anyMatch(field -> field.getAllInvolvedRawTypes().contains(javaClass)))
                    .anyMatch(dependency -> isUsedInRequestMapping(dependency.getOriginClass(), visited));
        }
    }

    private static class HaveUniqueSchemaNamesCondition extends ArchCondition<JavaClass> {
        private final Map<String, Set<JavaClass>> schemaNamesByClass = new HashMap<>();

        HaveUniqueSchemaNamesCondition() {
            super("have unique schema names");
        }

        private String getSchemaName(JavaClass clazz) {
            return ArchUnitMetaAnnotation.tryGet(Schema.class, clazz.getAnnotations())
                    .filter(schema -> !schema.name().isBlank())
                    .map(Schema::name)
                    .orElseGet(clazz::getSimpleName);
        }

        @Override
        public void check(JavaClass clazz, ConditionEvents events) {
            String schemaName = getSchemaName(clazz);
            schemaNamesByClass.computeIfAbsent(schemaName, k -> new HashSet<>()).add(clazz);
        }

        @Override
        public void finish(ConditionEvents events) {
            for (Map.Entry<String, Set<JavaClass>> entry : schemaNamesByClass.entrySet()) {
                String schemaName = entry.getKey();
                List<JavaClass> classes = new ArrayList<>(entry.getValue());
                classes.sort(Comparator.comparing(JavaClass::getFullName));

                if (classes.size() <= 1) {
                    continue;
                }

                JavaClass representative = classes.getFirst();
                String message = String.format("Schema name '%s' is not unique - used by:%n%s",
                        schemaName, getViolationLocationsAsString(classes));
                events.add(SimpleConditionEvent.violated(representative, message));
            }
        }

        private String getViolationLocationsAsString(List<JavaClass> classes) {
            return classes.stream()
                    .map(this::getViolationLocationAsString)
                    .collect(java.util.stream.Collectors.joining(System.lineSeparator()));
        }

        private String getViolationLocationAsString(JavaClass clazz) {
            return "  - " + clazz.getFullName();
        }
    }
}
