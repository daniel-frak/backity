package dev.codesoapbox.backity.core.shared.infrastructure.config.openapi;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import lombok.RequiredArgsConstructor;
import org.reflections.Reflections;
import org.springdoc.core.customizers.OpenApiCustomiser;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Provides objects annotated with {@code @IncludeInApiDocs} as additional schema in OpenAPI docs.
 *
 * @see IncludeInOpenApiDocs
 */
@RequiredArgsConstructor
public class OpenApiAdditionalSchemaProvider implements OpenApiCustomiser {

    private final Reflections reflections;

    @Override
    @SuppressWarnings("rawtypes")
    public void customise(OpenAPI openApi) {
        Map<String, Schema> schemas = openApi.getComponents()
                .getSchemas();

        ModelConverters modelConverters = ModelConverters.getInstance();
        getAdditionalClasses().forEach(c -> schemas.putAll(modelConverters.read(c)));
        getAdditionalEnums().forEach(e -> schemas.putAll(readEnumSchema(modelConverters, e)));
    }

    private Stream<Class<?>> getAdditionalClasses() {
        return reflections.getSubTypesOf(Object.class).stream()
                .filter(c -> c.isAnnotationPresent(IncludeInOpenApiDocs.class));
    }

    @SuppressWarnings("rawtypes")
    private Stream<Class<? extends Enum>> getAdditionalEnums() {
        return reflections.getSubTypesOf(Enum.class).stream()
                .filter(c -> c.isAnnotationPresent(IncludeInOpenApiDocs.class));
    }

    @SuppressWarnings("rawtypes")
    private Map<String, Schema> readEnumSchema(ModelConverters modelConverters, Class<? extends Enum> enumClass) {
        AnnotatedType enumType = new AnnotatedType().type(enumClass);
        return modelConverters.resolveAsResolvedSchema(enumType).referencedSchemas;
    }
}
