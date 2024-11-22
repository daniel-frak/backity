package dev.codesoapbox.backity.core.shared.adapters.driving.api.http.openapi;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.customizers.PropertyCustomizer;

import java.util.Arrays;
import java.util.Optional;

public class DeprecationInfoPropertyCustomizer implements PropertyCustomizer {

    public static final String DEPRECATED_SINCE_PREFIX = "Deprecated since ";

    @Override
    public Schema<?> customize(Schema property, AnnotatedType type) {
        Optional<Deprecated> deprecatedAnnotation = getDeprecatedAnnotation(type);

        if (deprecatedAnnotation.isEmpty() || deprecatedAnnotation.get().since().isBlank()) {
            return property;
        }

        String descriptionPrefix = DEPRECATED_SINCE_PREFIX + deprecatedAnnotation.get().since();

        String newDescription = property.getDescription() == null
                ? descriptionPrefix
                : (descriptionPrefix + ". " + property.getDescription());

        property.setDescription(newDescription);
        return property;
    }


    private Optional<Deprecated> getDeprecatedAnnotation(AnnotatedType type) {
        if (type.getCtxAnnotations() == null) {
            return Optional.empty();
        }

        return Arrays.stream(type.getCtxAnnotations())
                .filter(a -> a.annotationType() == Deprecated.class)
                .map(Deprecated.class::cast)
                .findFirst();
    }
}
