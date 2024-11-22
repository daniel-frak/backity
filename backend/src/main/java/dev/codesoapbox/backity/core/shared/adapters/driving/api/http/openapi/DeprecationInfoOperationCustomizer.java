package dev.codesoapbox.backity.core.shared.adapters.driving.api.http.openapi;

import io.swagger.v3.oas.models.Operation;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;

import java.util.Optional;

public class DeprecationInfoOperationCustomizer implements OperationCustomizer {

    public static final String DEPRECATED_SINCE_PREFIX = "Deprecated since ";

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        Optional<Deprecated> deprecatedAnnotation = getDeprecatedAnnotation(handlerMethod);

        if (deprecatedAnnotation.isEmpty() || deprecatedAnnotation.get().since().isBlank()) {
            return operation;
        }

        String descriptionPrefix = DEPRECATED_SINCE_PREFIX + deprecatedAnnotation.get().since();

        String newDescription = operation.getDescription() == null
                ? descriptionPrefix
                : (descriptionPrefix + ". " + operation.getDescription());

        operation.setDescription(newDescription);
        return operation;
    }


    private Optional<Deprecated> getDeprecatedAnnotation(HandlerMethod handlerMethod) {
        Deprecated deprecatedAnnotation = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), Deprecated.class);
        return Optional.ofNullable(deprecatedAnnotation);
    }
}
