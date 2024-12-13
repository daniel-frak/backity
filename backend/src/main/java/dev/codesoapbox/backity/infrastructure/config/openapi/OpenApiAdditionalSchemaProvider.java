package dev.codesoapbox.backity.infrastructure.config.openapi;

import dev.codesoapbox.backity.shared.adapters.driven.messaging.IncludeInDocumentation;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import lombok.RequiredArgsConstructor;
import org.reflections.Reflections;
import org.springdoc.core.customizers.OpenApiCustomizer;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Provides objects annotated with {@code @IncludeInApiDocs} as additional schema in OpenAPI docs.
 *
 * @see IncludeInDocumentation
 */
@RequiredArgsConstructor
public class OpenApiAdditionalSchemaProvider implements OpenApiCustomizer {

    private final Reflections reflections;

    @Override
    @SuppressWarnings("rawtypes")
    public void customise(OpenAPI openApi) {
        Map<String, Schema> schemas = openApi.getComponents()
                .getSchemas();
        ModelConverters modelConverters = ModelConverters.getInstance();
        getAdditionalClasses().forEach(c -> {
            Map<String, Schema> schemasToAdd = modelConverters
                    .resolveAsResolvedSchema(new AnnotatedType(c))
                    .referencedSchemas;
            schemas.putAll(schemasToAdd);
        });
        getAdditionalEnums().forEach(e -> schemas.putAll(readEnumSchema(modelConverters, e)));
    }

    private Stream<Class<?>> getAdditionalClasses() {
        Stream<Class<?>> classStream = reflections.getSubTypesOf(Object.class).stream()
                .filter(c -> c.isAnnotationPresent(IncludeInDocumentation.class));
        Stream<Class<? extends Record>> recordStream = reflections.getSubTypesOf(Record.class).stream()
                .filter(c -> c.isAnnotationPresent(IncludeInDocumentation.class));
        return Stream.concat(classStream, recordStream);
    }

    @SuppressWarnings("rawtypes")
    private Stream<Class<? extends Enum>> getAdditionalEnums() {
        return reflections.getSubTypesOf(Enum.class).stream()
                .filter(c -> c.isAnnotationPresent(IncludeInDocumentation.class));
    }

    @SuppressWarnings("rawtypes")
    private Map<String, Schema> readEnumSchema(ModelConverters modelConverters, Class<? extends Enum> enumClass) {
        AnnotatedType enumType = new AnnotatedType().type(enumClass);
        return modelConverters.resolveAsResolvedSchema(enumType).referencedSchemas;
    }
}
